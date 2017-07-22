
package com.example;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.amazonaws.services.sqs.model.QueueNameExistsException;
import com.example.model.Record;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class FileQueueService implements QueueService {


    //
    // Task 3: Implement me if you have time.
    //

    private Map<String, File> queueMap;

    private Map<String, Long> visibilityTimeoutMap;

    private File baseDir;

    private static String DATA_FILE_NAME = "message";

    private static String LOCK_FILE_NAME = "lock";

    private static String CONFIG_FILE_NAME = "config";

    private static final String SEPARATOR = ":";

    Clock getClock() {
        return clock;
    }

    void setClock(Clock clock) {
        this.clock = clock;
    }

    private Clock clock;


    public static FileQueueService getInstance() {
        return INSTANCE;
    }

    private static FileQueueService INSTANCE;

    static {
        try {
            INSTANCE = new FileQueueService();
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    private FileQueueService() throws IOException, InterruptedException {
        String dir1 = System.getProperty("queue.dir");
        String dir2 = System.getenv("queue_dir");
        String dir3 = System.getProperty("user.home") + File.separator + "queue_service";
        String dir = dir1 != null ? dir1 : (dir2 != null ? dir2 : dir3);
        ensureDir(dir);
        this.baseDir = new File(dir);
        this.clock = new Clock();
        queueMap = new HashMap<>();
        visibilityTimeoutMap = new HashMap<>();
        load();
    }

    private void ensureDir(String dirName) {
        if (dirName != null) {
            File dir = new File(dirName);
            if (!dir.exists()) {
                boolean result = dir.mkdirs();
                assert result;
            }
        }
    }

    private void load() throws IOException {
        for (File dir : this.baseDir.listFiles()) {
            if (dir.isDirectory()) {
                loadQueue(dir);
            }
        }
    }

    private void loadQueue(File dir) throws IOException {
        String queueName = dir.getName();
        for (File file : dir.listFiles()) {
            if (file.getName().equals(CONFIG_FILE_NAME)) {
                String visibilityTimeout = Files.readFirstLine(file, Charset.defaultCharset());
                visibilityTimeoutMap.put(queueName, Long.parseLong(visibilityTimeout));
            } else if (file.getName().equals(DATA_FILE_NAME)) {
                queueMap.put(queueName, file);
            }
        }

    }

    private void lock(File lock) throws InterruptedException {
        while (!lock.mkdir()) {
            Thread.sleep(50);
        }
    }

    private void unlock(File lock) {
        lock.delete();
    }


    @Override
    public void pushMessage(String queueName, String... messages) {
        validateQueueName(queueName);
        Long visibleFrom = clock.now();
        File messageFile = getMessageFile(queueName);
        File file = getLock(queueName);
        try {
            lock(file);
            try (PrintWriter pw = new PrintWriter(new FileWriter(messageFile, true))) {
                for (String message : messages) {
                    Record r = Record.create(visibleFrom, message);
                    pw.println(parseRecord(r));
                }
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            unlock(file);
        }
    }

    /**
     * parse a record as string, the format visibleFrom:receiptHandle:messageBody
     *
     * @param record
     * @return
     */
    private String parseRecord(Record record) {
        StringBuilder sb = new StringBuilder();
        sb.append(record.getVisibleFrom() != null ? record.getVisibleFrom() : 0L).append(SEPARATOR);
        sb.append(record.getReceiptHandle() != null ? record.getReceiptHandle() : "").append(SEPARATOR);
        sb.append(record.getMessageBody() != null ? record.getMessageBody() : "");
        return sb.toString();
    }

    /**
     * parse a string to a record,
     *
     * @param line the format visibleFrom:receiptHandle:messageBody
     * @return
     */
    private Record parseLineToRecord(String line) {
        String[] data = line.split(SEPARATOR);
        Long visibleFrom = Long.parseLong(data[0]);
        String receiptHandle = data[1].equals("") ? null : data[1];
        String messageBody = data[2].equals("") ? null : data[2];
        Record record = Record.create(visibleFrom, messageBody);
        record.setReceiptHandle(receiptHandle);
        return record;
    }


    private File getLock(String queueName) {
        return new File(this.baseDir.getAbsolutePath() + File.separator + queueName + File.separator + LOCK_FILE_NAME);

    }

    private File getMessageFile(String queueName) {
        return queueMap.get(queueName);
    }

    @Override
    public Message pullMessage(String queueName) {
        validateQueueName(queueName);
        File messageFile = getMessageFile(queueName);
        File lock = getLock(queueName);
        try {
            lock(lock);
            BufferedReader reader = new BufferedReader(new FileReader(messageFile));
            String line = null;
            Long now = clock.now();
            Long visibilityTimeout = TimeUnit.SECONDS.toMillis(visibilityTimeoutMap.get(queueName));
            Message message = null;
            List<String> allMessage = new LinkedList<>();
            while ((line = reader.readLine()) != null) {
                Record record = parseLineToRecord(line);
                if (message == null && record.getVisibleFrom() <= now) {
                    String receiptHandle = UUID.randomUUID().toString();
                    record.setReceiptHandle(receiptHandle);
                    record.setVisibleFrom(now + visibilityTimeout);
                    message = new Message();
                    message.withMessageId(UUID.randomUUID().toString()).withReceiptHandle(receiptHandle).withBody(record.getMessageBody());
                    allMessage.add(parseRecord(record));
                } else {
                    allMessage.add(line);
                }
            }
            File tempFile = createFileUnderDir(messageFile.getParentFile(), DATA_FILE_NAME + ".tmp");
            appendLineToFile(tempFile, allMessage.toArray(new String[]{}));
            replaceFile(tempFile, messageFile);
            return message;
        } catch (InterruptedException | IOException e) {
            throw new AmazonServiceException("pull message fail.", e);
        } finally {
            unlock(lock);
        }
    }

    private void replaceFile(File newFile, File oldFile) throws IOException {
        Files.copy(newFile, oldFile);
        newFile.delete();
    }


    private void appendLineToFile(File to, String... lines) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(to, true))) {
            for (String line : lines) {
                pw.println((line));
            }
        }
    }


    private void validateQueueName(String queueName) {
        if (!this.queueMap.containsKey(queueName)) {
            throw new QueueDoesNotExistException(queueName);
        }
    }

    @Override
    public void deleteMessage(String queueName, String receiptHandle) {
        validateQueueName(queueName);
        File lock = getLock(queueName);
        File messageFile = getMessageFile(queueName);
        try {
            lock(lock);
            BufferedReader reader = new BufferedReader(new FileReader(messageFile));
            String line = null;
            List<String> restMessage = new LinkedList<>();
            boolean find = false;
            while ((line = reader.readLine()) != null) {
                if (!line.contains(receiptHandle)) {
                    restMessage.add(line);
                } else {
                    find = true;
                }
            }
            // the receiptHandle is valid,and file one
            if (find) {
                File tempFile = createFileUnderDir(messageFile.getParentFile(), DATA_FILE_NAME + ".tmp");
                appendLineToFile(tempFile, restMessage.toArray(new String[]{}));
                replaceFile(tempFile, messageFile);
            }
        } catch (InterruptedException | IOException e) {
            throw new AmazonServiceException("delete message fail", e);
        } finally {
            unlock(lock);
        }
    }

    @Override
    public synchronized String createQueue(String queueName, Long visibilityTimeout) {

        Preconditions.checkArgument(queueName != null && queueName.length() != 0);
        Preconditions.checkArgument(visibilityTimeout >= 0);
        if (this.visibilityTimeoutMap.get(queueName) != null && !this.visibilityTimeoutMap.get(queueName).equals(visibilityTimeout)) {
            throw new QueueNameExistsException(String.format("A queue already exists with the same name[%s] and a different value for attribute VisibilityTimeout", queueName));
        } else if (!this.queueMap.containsKey(queueName)) {
            try {
                File dataDir = createDirUnderDir(this.baseDir, queueName);
                File dataFile = createFileUnderDir(dataDir, DATA_FILE_NAME);
                File configFile = createFileUnderDir(dataDir, CONFIG_FILE_NAME);
                Files.asCharSink(configFile, Charset.defaultCharset()).write(String.valueOf(visibilityTimeout) + "\n");
                queueMap.put(queueName, dataFile);
                visibilityTimeoutMap.put(queueName, visibilityTimeout);
                return dataFile.getAbsolutePath();
            } catch (IOException e) {
                throw new AmazonServiceException("create queue fail", e);
            }
        } else {
            return this.queueMap.get(queueName).getAbsolutePath();
        }
    }


    /**
     * create a file under a dir if it does not exist
     *
     * @return true if the named file does not exist and was
     * successfully created; false if the named file
     * already exists
     * @throws IOException
     */
    private File createFileUnderDir(File dir, String fileName) throws IOException {
        File newFile = new File(dir.getAbsolutePath() + File.separator + fileName);
        newFile.createNewFile();
        return newFile;
    }

    private File createDirUnderDir(File dir, String subDirName) {
        File subDir = new File(dir.getAbsolutePath() + File.separator + subDirName);
        subDir.mkdir();
        return subDir;

    }

    @VisibleForTesting
    synchronized void cleanAllData() {
        deleteFileUnderDir(this.baseDir);
        this.queueMap.clear();
        this.visibilityTimeoutMap.clear();
    }

    private void deleteFileUnderDir(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                deleteFileUnderDir(file);
            }
            file.delete();
        }
    }

}


