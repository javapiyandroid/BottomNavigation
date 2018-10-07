package com.downloadmanager;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.microsoft.azure.storage.blob.BlobURL;
import com.microsoft.azure.storage.blob.BlockBlobURL;
import com.microsoft.azure.storage.blob.ContainerURL;
import com.microsoft.azure.storage.blob.PipelineOptions;
import com.microsoft.azure.storage.blob.ServiceURL;
import com.microsoft.azure.storage.blob.SharedKeyCredentials;
import com.microsoft.azure.storage.blob.StorageURL;
import quickstart.FilefilterClass;

public class DownloadTask {

	private static final int COUNTTHREADS = 20;

	List<String> filePathList;

	/*
	 * public DownloadTask() {
	 *
	 * this.fileSource=fileSource; }
	 */
	public DownloadTask(List<String> filePathList) {

		this.filePathList = filePathList;
	}

	public void downloadChunk() throws InvalidKeyException, IOException {

		ContainerURL containerURL;
		ExecutorService exe = Executors.newFixedThreadPool(COUNTTHREADS);

		for (int jj = 0; jj < filePathList.size(); jj++) {
			String accountName = "codos1";
			String accountKey = "MVMF3m1GM/+ficIoLQtP/d1QbgVvTbvfXsjxUW6EBtgmO31NHnLDPCsTQJSc1IC4MZ1FeeWDySebhYdWtz0wKg==";

			File sourceFile = new File(filePathList.get(jj));
			SharedKeyCredentials creds = new SharedKeyCredentials(accountName, accountKey);
			// We are using a default pipeline here, you can learn more about it at
			// https://github.com/Azure/azure-storage-java/wiki/Azure-Storage-Java-V10-Overview
			final ServiceURL serviceURL = new ServiceURL(new URL("https://" + accountName + ".blob.core.windows.net"),
					StorageURL.createPipeline(creds, new PipelineOptions()));
			containerURL = serviceURL.createContainerURL("pd-quickstart1");

			BlockBlobURL blobURL = containerURL.createBlockBlobURL(filePathList.get(jj));


			Runnable worker = new BlobDownloads(blobURL, sourceFile);

			//	merge(sourceFile);
			exe.execute(worker);

		}

		exe.shutdown();
		//// Wait until all threads are finish
		while (!exe.isTerminated()) {

		}
		System.out.println("\nFinished all threads" + "length" + filePathList.size());
		merging();
		moving();

	}

	private void moving() throws IOException {
		ProcessBuilder builder = new ProcessBuilder(
				"cmd.exe", "/c", "cd \"F:\\OSproject\\storage-blobs-java-v10-quickstart-master\" && for /r %f in (*.txt) do move /y %f F:\\filesblob");
		builder.redirectErrorStream(true);
		Process p = builder.start();
		System.out.println("Files moved to F:\\filesblob");
		/*ProcessBuilder builder = new ProcessBuilder(
				"cmd.exe", "/c", "cd \\path");*/
	}

	private void merging() throws IOException {

		ProcessBuilder builder = new ProcessBuilder(
				"cmd.exe", "/c", "cd \"F:\\OSproject\\storage-blobs-java-v10-quickstart-master\" && for %f in (*.txt) do type %f >> Blobmerge.txt");
		builder.redirectErrorStream(true);
		Process p = builder.start();
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while (true) {
			line = r.readLine();
			if (line == null) { break; }
			System.out.println(line);
		}

	}
	}

