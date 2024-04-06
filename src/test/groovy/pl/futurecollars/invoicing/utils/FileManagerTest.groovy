package pl.futurecollars.invoicing.utils


import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class FileManagerTest extends Specification {

    def "create() should create a file when it doesn't exist"() {
        given:
        def fileManager = new FileManager()
        def file = new File("TEST FILE.txt")

        when:
        fileManager.createFile(file)

        then:
        file.exists()

        cleanup:
        Files.deleteIfExists(Path.of("TEST FILE.txt"))
    }

    def "create() should throw an exception when file already exists"() {
        given:
        def fileManager = new FileManager()
        def existingFile = new File("TEST FILE.txt")
        existingFile.createNewFile()

        when:
        fileManager.createFile(existingFile)

        then:
        thrown(IOException.class)

        cleanup:
        Files.deleteIfExists(Path.of("TEST.txt"))
    }

    def "delete() called should delete a file if exists"() {
        given:
        def fileManager = new FileManager()
        def testFile = new File("TEST.txt")
        testFile.createNewFile()

        when:
        fileManager.deleteFile(testFile)

        then:
        !testFile.exists()

        cleanup:
        Files.deleteIfExists(Path.of("TEST.txt"))
    }

    def "delete() should throw an exception if file not exists"() {
        given:
        FileManager fileManager = new FileManager()
        File testFile = new File("TEST FILE.txt")
        testFile.createNewFile()

        when:
        fileManager.deleteFile(testFile)
        fileManager.validateFileExistance(testFile, "message")

        then:
        thrown(FileNotFoundException.class)

        cleanup:
        Files.deleteIfExists(Path.of("TEST FILE.txt"))
    }

    def "copyFile() should make a copy of file to another"() {
        given:
        FileManager fileManager = new FileManager()
        Path fromPath = Path.of("INVOICE_TEST.txt")
        File sourceFile = new File(fromPath.toString())
        Path toPath = Path.of("NEW FILE.txt")
        File newFile = new File(toPath.toString())

        sourceFile.createNewFile()
        Files.writeString(fromPath, "Sample text line", StandardOpenOption.WRITE)

        when:
        fileManager.copyFile(fromPath, toPath)

        then:
        Files.readString(toPath) == "Sample text line"

        cleanup:
        Files.deleteIfExists(Path.of("INVOICE_TEST.txt"))
        Files.deleteIfExists(Path.of("NEW FILE.txt"))
    }

    def "makeBackupFile() should make a backup while operation on file"() {
        given:
        FileManager fileManager = new FileManager()
        Path source = Path.of("INVOICE_TEST.txt")
        File sourceFile = new File(source.toString())
        File backupFile = new File("[INVOICE_TEST.txt]_BACKUP.txt")
        Files.createFile(source)

        when:
        fileManager.makeBackupFile(source)

        then:
        source.toFile().exists()
        backupFile.exists()
        backupFile.getName() == "[INVOICE_TEST.txt]_BACKUP.txt"

        cleanup:
        Files.deleteIfExists(Path.of("[INVOICE_TEST.txt]_BACKUP.txt"))
        Files.deleteIfExists(Path.of("INVOICE_TEST.txt"))
    }

    def "makeBackupFile() should throw an exception when file already exists"() {
        given:
        FileManager fileManager = new FileManager()
        Path source = Path.of("INVOICE_TEST.txt")
        Path backup = Path.of("[INVOICE_TEST.txt]_BACKUP.txt")
        Files.createFile(source)
        Files.createFile(backup)

        when:
        fileManager.makeBackupFile(source)

        then:
        source.toFile().exists()
        backup.toFile().exists()
        thrown(RuntimeException.class)

        cleanup:
        Files.deleteIfExists(Path.of("[INVOICE_TEST.txt]_BACKUP.txt"))
        Files.deleteIfExists(Path.of("INVOICE_TEST.txt"))
    }

    def "deleteBackupFile() should throw an exception when source file not exists"() {
        given:
        FileManager fileManager = new FileManager()
        Path source = Path.of("INVOICE_TEST.txt")
        Path backup = Path.of("[INVOICE_TEST.txt]_BACKUP.txt")
        Files.createFile(backup)

        when:
        fileManager.deleteBackupFile(source)

        then:
        backup.toFile().exists()
        thrown(RuntimeException.class)

        cleanup:
        Files.deleteIfExists(Path.of("[INVOICE_TEST.txt]_BACKUP.txt"))
        Files.deleteIfExists(Path.of("INVOICE_TEST.txt"))
    }

    def "deleteBackupFile() should delete backup file when source file exists"() {
        given:
        FileManager fileManager = new FileManager()
        Path source = Path.of("INVOICE_TEST.txt")
        Path backup = Path.of("[INVOICE_TEST.txt]_BACKUP.txt")
        Files.createFile(source)
        Files.createFile(backup)

        when:
        fileManager.deleteBackupFile(source)
        source.toFile().exists()

        then:
        source.toFile().exists()
        !backup.toFile().exists()

        cleanup:
        Files.deleteIfExists(Path.of("[INVOICE_TEST.txt]_BACKUP.txt"))
        Files.deleteIfExists(Path.of("INVOICE_TEST.txt"))
    }
}
