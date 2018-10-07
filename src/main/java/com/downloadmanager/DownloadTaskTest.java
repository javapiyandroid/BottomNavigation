package com.downloadmanager;


import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

public class DownloadTaskTest {


    @Test
    public void  TestDownload() throws IOException, InvalidKeyException {

    ArrayList<String> filePath=new ArrayList<>();



        filePath.add("SampleBlob.txt");
        filePath.add("SampleBlob1.txt");
        filePath.add("SampleBlob2.txt");
        filePath.add("SampleBlob3.txt");
        filePath.add("SampleBlob4.txt");

        DownloadTask d=new DownloadTask(filePath);
        d.downloadChunk();


    }
}