package jahspotify;

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

import jahspotify.util.*;
import org.apache.commons.logging.*;

/**
 * @author Johan Lindquist
 */
public class JahSpotifyNativeLoader
{

    public static boolean loadJahSpotify()
    {
        JNILibraryLoader jniLibraryLoader = new JNILibraryLoader();
        jniLibraryLoader.setLeaveExtracted(true);
        jniLibraryLoader.setOverwrite(true);
        try
        {
            jniLibraryLoader.loadLibrary("jahspotify");
            return true;
        }
        catch (JNILibraryLoaderException e)
        {
            LogFactory.getLog(JahSpotifyNativeLoader.class).error("Error loading JahSpotify native library: " + e.getMessage(), e);
            return false;
        }

    }

}
