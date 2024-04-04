package pl.futurecollars.invoicing.utils

import pl.futurecollars.invoicing.db.file.PathProvider
import spock.lang.Specification

import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class FileManagerTest extends Specification {

    def "create() should create a file when it doesn't exist"() {
        given:
        def pathProvider = new PathProvider("TEST FILE.txt", "ID FILE.txt")
        def fileManager = new FileManager(/*pathProvider*/)
        def file = new File(pathProvider.getInvoicePath().toString())

        when:
        fileManager.createFile(file)

        then:
        file.exists()

        cleanup:
        Files.deleteIfExists(Path.of("TEST FILE.txt"))
        Files.deleteIfExists(Path.of("ID FILE.txt"))
    }

    def "create() should throw an exception when file already exists"() {
        given:
        def pathProvider = new PathProvider("TEST.txt", "ID.txt")
        def fileManager = new FileManager(/*pathProvider*/)
        def existingFile = new File(pathProvider.getInvoicePath().toString())
        existingFile.createNewFile()

        when:
        fileManager.createFile(existingFile)

        then:
        thrown(IOException.class)

        cleanup:
        Files.deleteIfExists(Path.of("TEST.txt"))
        Files.deleteIfExists(Path.of("ID.txt"))
    }

    def "delete() called should delete a file if exists"() {
        given:
        def pathProvider = new PathProvider("TEST.txt", "ID.txt")
        def fileManager = new FileManager(/*pathProvider*/)
        def testFile = new File(pathProvider.getInvoicePath().toString())
        testFile.createNewFile()

        when:
        fileManager.deleteFile(testFile)

        then:
        !testFile.exists()

        cleanup:
        Files.deleteIfExists(Path.of("TEST.txt"))
        Files.deleteIfExists(Path.of("ID.txt"))
    }

    def "delete() should throw an exception if file not exists"() {
        given:
        PathProvider pathProvider = new PathProvider("TEST FILE.txt", "ID FILE.txt")
        FileManager fileManager = new FileManager(/*pathProvider*/)
        File testFile = new File("TEST FILE.txt")
        testFile.createNewFile()

        when:
        fileManager.deleteFile(testFile)
        fileManager.validateFileExistance(testFile, "message")

        then:
        thrown(FileNotFoundException.class)

        cleanup:
        Files.deleteIfExists(Path.of("TEST FILE.txt"))
        Files.deleteIfExists(Path.of("ID FILE.txt"))
    }

    /*def "moveTo() should replace a file to another"() {
        given:
        PathProvider pathProvider = new PathProvider("TEST FILE.txt", "ID_FILE.txt")
        File testFile = new File("TEST FILE.txt")
        testFile.createNewFile()
        Path toPath = Paths.get("NEW FILE.txt")
        FileManager fileManager = new FileManager(pathProvider)

        when:
        fileManager.moveTo(toPath)

        then:
        !testFile.exists()
        toPath.toFile().exists()

        cleanup:
        Files.deleteIfExists(Path.of("TEST FILE.txt"))
        Files.deleteIfExists(Path.of("NEW FILE.txt"))
        Files.deleteIfExists(Path.of("ID_FILE.txt"))
    }*/

    def "copyFile() should make a copy of file to another"() {
        given:
        PathProvider pathProvider = new PathProvider("INVOICE_TEST.txt", "ID_TEST.txt")
        FileManager fileManager = new FileManager(/*pathProvider*/)
        Path fromPath = Path.of(pathProvider.getInvoicePath().toString())
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
        Files.deleteIfExists(Path.of("ID_TEST.txt"))
        Files.deleteIfExists(Path.of("NEW FILE.txt"))
    }

    def "makeBackupFile() should make a backup while operation on file"() {
        given:
        PathProvider pathProvider = new PathProvider("INVOICE_TEST.txt", "ID_TEST.txt")
        FileManager fileManager = new FileManager(/*pathProvider*/)
        Path source = Path.of(pathProvider.getInvoicePath().toString())
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
        Files.deleteIfExists(Path.of("ID_TEST.txt"))
    }

    def "makeBackupFile() should throw an exception when file already exists"() {
        given:
        /*PathProvider pathProvider = new PathProvider("INVOICE_TEST.txt", "ID_TEST.txt")*/
        FileManager fileManager = new FileManager(/*pathProvider*/)
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
        Files.deleteIfExists(Path.of("ID_TEST.txt"))
    }

    def "deleteBackupFile() should throw an exception when source file not exists"() {
        given:
        /*PathProvider pathProvider = new PathProvider("INVOICE_TEST.txt", "ID_TEST.txt")*/
        FileManager fileManager = new FileManager(/*pathProvider*/)
        Path source = Path.of("INVOICE_TEST.txt")
        Path backup = Path.of("[INVOICE_TEST.txt]_BACKUP.txt")
        Files.createFile(backup)

        when:
        fileManager.deleteBackupOfFile(source)

        then:
        backup.toFile().exists()
        thrown(RuntimeException.class)

        cleanup:
        Files.deleteIfExists(Path.of("[INVOICE_TEST.txt]_BACKUP.txt"))
        Files.deleteIfExists(Path.of("INVOICE_TEST.txt"))
        Files.deleteIfExists(Path.of("ID_TEST.txt"))
    }

    def "deleteBackupFile() should delete backup file when source file exists"() {
        given:
        /*PathProvider pathProvider = new PathProvider("INVOICE_TEST.txt", "ID_TEST.txt")*/
        FileManager fileManager = new FileManager(/*pathProvider*/)
        Path source = Path.of("INVOICE_TEST.txt")
        Path backup = Path.of("[INVOICE_TEST.txt]_BACKUP.txt")
        Files.createFile(source)
        Files.createFile(backup)

        when:
        fileManager.deleteBackupOfFile(source)
        source.toFile().exists()

        then:
        source.toFile().exists()
        !backup.toFile().exists()

        cleanup:
        Files.deleteIfExists(Path.of("[INVOICE_TEST.txt]_BACKUP.txt"))
        Files.deleteIfExists(Path.of("INVOICE_TEST.txt"))
        Files.deleteIfExists(Path.of("ID_TEST.txt"))
    }
}
