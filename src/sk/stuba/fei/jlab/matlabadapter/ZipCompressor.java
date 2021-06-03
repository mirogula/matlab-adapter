package sk.stuba.fei.jlab.matlabadapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipCompressor {

	private String rootDir;
	private List<File> files;
	private String zipFileName;

	public ZipCompressor(String rootDir, List<String> files, String zipFileName) throws IOException {
		File rootDirFile = new File(rootDir);
		if (!rootDirFile.isDirectory()) {
			throw new IOException("rootDir must be directory");
		}
		this.rootDir = rootDirFile.getAbsolutePath();
		this.zipFileName = zipFileName;
		this.files = new ArrayList<>();
		for (String fileName : files) {
			File file = new File(fileName);
			if (file.isAbsolute() && file.getParentFile().equals(rootDirFile)) {
				this.files.add(file);
			} else if ((file = new File(this.rootDir, fileName)).exists()) {
				this.files.add(file);
			}
		}
	}

	public void compress() throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFileName))) {
			for (File node : files) {
				addFileToZip(zos, node);
			}
		}
	}

	private void addFileToZip(ZipOutputStream zos, File node)
			throws IOException {
		if (node.isDirectory()) {
			for (File file : node.listFiles()) {
				addFileToZip(zos, file);
			}
		}
		if (node.isFile()) {
			try (FileInputStream fis = new FileInputStream(node)) {
				byte buffer[] = new byte[1024];
				zos.putNextEntry(new ZipEntry(getRelativePath(node.getAbsolutePath()).replaceAll("\\\\", "/")));

				int len;
				while ((len = fis.read(buffer)) != -1) {
					zos.write(buffer, 0, len);
				}
				zos.closeEntry();
			}
		}
	}

	private String getRelativePath(String file) {
		return file.substring(rootDir.length() + 1, file.length());
	}

}
