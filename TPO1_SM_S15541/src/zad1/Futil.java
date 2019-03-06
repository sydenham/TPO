package zad1;

import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.*;
import java.io.IOException;
import java.nio.file.SimpleFileVisitor;

public class Futil extends SimpleFileVisitor<Path> {
	
	//Strumien wyjscia i tworzenie pliku
	static FileOutputStream out;
	static String resultFileName;
	
	//metoda przetwarzania podanego folderu 
	public static void processDir(String dirFileName, String rfn) {
		
		resultFileName = rfn;
		Path dirPath = Paths.get(dirFileName);
		Path resultPath = Paths.get(resultFileName);
		
		//sprawdzanie czy istnieje folder wejsciowy i czy to folder
		//sprawdzanie czy istnieje plik docelowy i tworzenie go lub czyszczenie
		if (Files.notExists(dirPath)) {
			System.out.println("Sciezka zrodłowa nie istnieje");
			return;
		} else if (!Files.isDirectory(dirPath)) {
			System.out.println("Sciezka zrodłowa to nie katalog");
			return;
		} else if (Files.notExists(resultPath)) {
			try {
				out = new FileOutputStream(resultFileName, true);
				System.out.println("Tworze plik docelowy");
			} catch (FileNotFoundException e) {
				System.out.println("NIe udalo sie utworzyc polaczenia z docelowym plikiem");
				e.printStackTrace();
			}
		} else {
			try {
				out = new FileOutputStream(resultFileName, true);
				PrintWriter writer = new PrintWriter(resultFileName);
				writer.write("");
				writer.flush();
				writer.close();
				System.out.println("Plik docelowy istnieje i zostal wyczyszczony");
			} catch (FileNotFoundException e) {
				System.out.println("NIe udalo sie utworzyc polaczenia z docelowym plikiem");
				e.printStackTrace();
			}
		}
		//tworzenie obiektu futil bedacego FileVisitorem i przechodzenie przez drzewo
		try {
			Futil visitor = new Futil();
			Files.walkFileTree(dirPath, visitor);
			System.out.println("Zakonczono przechodzenie przez drzewo");
		} catch (IOException e) {
			System.out.println("Nie udalo sie przejsc przez drzewo");
			e.printStackTrace();
		}

	}
	//metoda przesylajaca z kodowaniem z cp1250 na utf8, przez bajty char
	public void transferWithEncoding(String dirFileName) throws Exception {

//		Kod ponizszy bylby najlepszy, gdyz bylby to bezposredni przesyl z A do B
//		FileInputStream in = new FileInputStream(arg0.toString());
//		FileChannel fcin = in.getChannel();
//		FileChannel fcout = new FileOutputStream(resultFileName, true).getChannel();
//		fcout.position( fcout.size() );
//		fcin.transferTo(0, fcin.size(), fcout);

		FileChannel fcin = new FileInputStream(dirFileName).getChannel();
		FileChannel fcout = new FileOutputStream(resultFileName, true).getChannel();
		ByteBuffer buf = ByteBuffer.allocate((int) fcin.size());

		// czytanie z kanału
		fcin.read(buf);

		// Strony kodowe
		Charset inCharset = Charset.forName("Cp1250"), outCharset = Charset.forName("UTF-8");

		// dekodowanie bufora bajtowego
		buf.flip();
		CharBuffer cbuf = inCharset.decode(buf);

		// enkodowanie bufora znakowego
		// i zapis do pliku poprzez kanał

		buf = outCharset.encode(cbuf);
		fcout.write(buf);

		fcin.close();
		fcout.close();
	}
	// ponizsze metody overriduja te metody z klasy SimpleFileVisitor<Path>
	//kontynuujemy zwiedzanie po zwiedzeniu danego folderu
	public FileVisitResult postVisitDirectory(Path arg0, IOException arg1) throws IOException {
		System.out.println("Just visited " + arg0);
		return FileVisitResult.CONTINUE;
	}
	//kontynuujemy zwiedzanie po wykryciu kolejnego folderu
	public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes fileAttributes) throws IOException {
		System.out.println("About to visit " + arg0);
		return FileVisitResult.CONTINUE;
	}
	//znaleziony w folderze plik przesylamy metoda transferWithEncoding i kontynuujemy zwiedzanie
	public FileVisitResult visitFile(Path dirFileName, BasicFileAttributes fileAttributes) {
		if (fileAttributes.isRegularFile()) {
			try {
				transferWithEncoding(dirFileName.toString());
			} catch (Exception e) {
				System.out.println("Transfer sie nie powiodl");
				e.printStackTrace();
			}
		}
		return FileVisitResult.CONTINUE;
	}
	//jesli zwiedzanie nie powiedzie sie to blad
	public FileVisitResult visitFileFailed(Path arg0, IOException arg1) throws IOException {
		System.err.print(arg1.getMessage());
		return super.visitFileFailed(arg0, arg1);
	}

}
