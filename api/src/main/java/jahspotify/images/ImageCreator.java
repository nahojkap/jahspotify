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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Johan Lindquist
 */
public class ImageCreator
{

    public static byte[] createImage(int squareSize, List<byte[]> imageBytes) throws IOException
    {
        List<BufferedImage> images = new ArrayList<BufferedImage>();
        for (byte[] imageByte : imageBytes)
        {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageByte));
            images.add(image);
        }

        int imageWidth = images.size() == 1 ? squareSize : squareSize * 2;
        int imageHeight = imageWidth;

        BufferedImage combined = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        // paint both images, preserving the alpha channels
        Graphics g = combined.getGraphics();


        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,0,imageWidth,imageHeight);

        g.drawImage(images.get(0), 0, 0, null);

        final BufferedImage imageBuff;

        if (images.size() == 1)
        {
            imageBuff = combined;
        }
        else
        {
            if (images.size() == 2)
            {
                g.drawImage(images.get(1), squareSize, squareSize, null);
            }
            else
            {
                g.drawImage(images.get(1), squareSize, 0, null);

                if (images.size() == 3)
                {
                    g.drawImage(images.get(2), (int) (imageHeight - squareSize) / 2, squareSize, null);
                }
                else
                {
                    g.drawImage(images.get(2), 0, squareSize, null);
                    if (images.size() == 4)
                    {
                        g.drawImage(images.get(3), squareSize, squareSize, null);
                    }
                }

            }

            final Image scaledImage = combined.getScaledInstance(300, 300, Image.SCALE_SMOOTH);

            imageBuff = new BufferedImage(300,300, BufferedImage.TYPE_INT_RGB);
            g = imageBuff.createGraphics();
            g.drawImage(scaledImage, 0, 0, null);
            g.dispose();

        }

        // Save as new image
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(imageBuff, "PNG", output);

        return output.toByteArray();
    }


}
