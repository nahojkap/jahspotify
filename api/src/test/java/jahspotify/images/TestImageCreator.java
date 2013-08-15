package jahspotify.images;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *        or more contributor license agreements.  See the NOTICE file
 *        distributed with this work for additional information
 *        regarding copyright ownership.  The ASF licenses this file
 *        to you under the Apache License, Version 2.0 (the
 *        "License"); you may not use this file except in compliance
 *        with the License.  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Unless required by applicable law or agreed to in writing,
 *        software distributed under the License is distributed on an
 *        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *        KIND, either express or implied.  See the License for the
 *        specific language governing permissions and limitations
 *        under the License.
 */

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Johan Lindquist
 */
public class TestImageCreator extends TestCase
{

   /* private byte[] ingestImage(InputStream inputStream) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int size = 1024*16;
        byte[] buffer = new byte[size];
        int len = inputStream.read(buffer,0,size);
        while (len != -1)
        {
            baos.write(buffer,0,len);
            len = inputStream.read(buffer,0,size);
        }
        baos.flush();
        baos.close();
        return baos.toByteArray();
    }

    public void testMakeImage4() throws IOException {

        List<byte[]> imageData = new ArrayList<byte[]>();
        byte[] readData1 = ingestImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("9ea9681bf16a33b61965418dc91d2787fb6379b3.jpeg"));
        imageData.add(readData1);
        readData1 = ingestImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("059994c86a7ff20b0b8d7a4e47049505ea4dea7a.jpeg"));
        imageData.add(readData1);
        readData1 = ingestImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("230a9831b9a4c516c1fb00b60deeb82a49a3960f.jpeg"));
        imageData.add(readData1);
        readData1 = ingestImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("60f115573c982b8e9abb4a672a7c20c507074e05.jpeg"));
        imageData.add(readData1);

        byte[] bytes = ImageCreator.createImage(300,imageData);


        FileOutputStream fos = new FileOutputStream("/tmp/image-4.png");
        fos.write(bytes);
        fos.flush();
        fos.close();


    }

    public void testMakeImage1() throws IOException {

        List<byte[]> imageData = new ArrayList<byte[]>();

        byte[] readData1 = ingestImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("9ea9681bf16a33b61965418dc91d2787fb6379b3.jpeg"));
        imageData.add(readData1);

        byte[] bytes = ImageCreator.createImage(300,imageData);

        FileOutputStream fos = new FileOutputStream("/tmp/image-1.png");
        fos.write(bytes);
        fos.flush();
        fos.close();


    }


    public void testMakeImage3() throws IOException {

        List<byte[]> imageData = new ArrayList<byte[]>();

        byte[] readData1 = ingestImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("9ea9681bf16a33b61965418dc91d2787fb6379b3.jpeg"));
        imageData.add(readData1);
        readData1 = ingestImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("059994c86a7ff20b0b8d7a4e47049505ea4dea7a.jpeg"));
        imageData.add(readData1);
        readData1 = ingestImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("230a9831b9a4c516c1fb00b60deeb82a49a3960f.jpeg"));
        imageData.add(readData1);

        byte[] bytes = ImageCreator.createImage(300,imageData);

        FileOutputStream fos = new FileOutputStream("/tmp/image-3.png");
        fos.write(bytes);
        fos.flush();
        fos.close();


    }

    public void testMakeImage2() throws IOException {

            List<byte[]> imageData = new ArrayList<byte[]>();

            byte[] readData1 = ingestImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("9ea9681bf16a33b61965418dc91d2787fb6379b3.jpeg"));
            imageData.add(readData1);
            readData1 = ingestImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("059994c86a7ff20b0b8d7a4e47049505ea4dea7a.jpeg"));
            imageData.add(readData1);

            byte[] bytes = ImageCreator.createImage(300,imageData);

            FileOutputStream fos = new FileOutputStream("/tmp/image-2.png");
            fos.write(bytes);
            fos.flush();
            fos.close();


        }*/

    public void testNothing() throws Exception
    {


    }
}
