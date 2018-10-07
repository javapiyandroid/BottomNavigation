package com.downloadmanager;

import java.io.*;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import com.microsoft.azure.storage.blob.BlobRange;
import com.microsoft.azure.storage.blob.BlockBlobURL;
import com.microsoft.rest.v2.util.FlowableUtil;
import com.sun.javafx.util.Utils;
import quickstart.FilefilterClass;

public class BlobDownloads implements Runnable {

	public BlockBlobURL blob;
	public File sourceFile;

	public BlobDownloads(BlockBlobURL blob, File sourceFile) {
	
		this.blob = blob;
		this.sourceFile = sourceFile;
	}

	public void getBlob(BlockBlobURL blobURL, File sourceFile) {
		try {
			// Get the blob using the low-level download method in BlockBlobURL type
			// com.microsoft.rest.v2.util.FlowableUtil is a static class that contains
			// helpers to work with Flowable
			// BlobRange is defined from 0 to 4MB



			blobURL.download(new BlobRange().withOffset(0).withCount(4 * 1024 * 1024L), null, false, null)
					.flatMapCompletable(response -> {
						AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get(sourceFile.getPath()),
								StandardOpenOption.CREATE, StandardOpenOption.WRITE);

						return FlowableUtil.writeFile(response.body(null), channel);
					})
					.doOnComplete(
							() -> System.out.println("All blobs was downloaded to " + sourceFile.getAbsolutePath()))

					.blockingAwait();


		} catch (Exception ex) {

			System.out.println(ex.toString());
		}
	}




	@Override
	public void run() {

		System.out.println("inside thread run for parallel operations");
		try {

			getBlob(this.blob, this.sourceFile);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
