package jahspotify.util;

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

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.*;

import org.apache.commons.logging.*;

/**
 * @author Johan Lindquist
 */
public class JNILibraryLoader
{
    private Log _log = LogFactory.getLog(JNILibraryLoader.class);
    private boolean _leaveExtracted;
    private boolean _overwrite = false;
    private File _extractionDirectory;
    private String _extractionDirectoryStr = System.getProperty("java.io.tmpdir");

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.US).replace(" ", "");
    private static final String OS_ARCH = System.getProperty("os.arch").toLowerCase(Locale.US);

    private static Set<String> _loadedLibraries = new HashSet<String>();
    private static Lock _libraryLoadLock = new ReentrantLock();

    public String createNativeLibraryName(final String libraryName)
    {
        return libraryName + OS_NAME + "-" + OS_ARCH;
    }

    public void loadLibrary(final String libname) throws JNILibraryLoaderException
    {
        loadLibrary(null, libname);
    }

    public void loadMappedLibrary(final String libname) throws JNILibraryLoaderException
    {
        loadMappedLibrary(null, libname);
    }

    public void loadMappedLibrary(final String pathPrefix, final String libname) throws JNILibraryLoaderException
    {
        loadLibraryInternal(pathPrefix, libname);
    }

    public void loadLibrary(final String pathPrefix, final String libname) throws JNILibraryLoaderException
    {
        final String mappedlib = System.mapLibraryName(libname);
        loadLibraryInternal(pathPrefix, mappedlib);
    }

    private void loadLibraryInternal(final String pathPrefix, final String mappedlib) throws JNILibraryLoaderException
    {
        _libraryLoadLock.lock();
        try
        {
            if (_loadedLibraries.contains(mappedlib))
            {
                _log.info("Library '" + mappedlib + "' has already been loaded, will ignore load call");
                return;
            }
            final File jniLibraryFile = extractResource((pathPrefix == null ? "" : pathPrefix) + mappedlib, mappedlib);
            System.load(jniLibraryFile.getAbsolutePath());
            _loadedLibraries.add(mappedlib);
        }
        finally
        {
            _libraryLoadLock.unlock();
        }
    }

    /**
     * extract a resource to the tmp dir (this entry point is used for unit testing)
     *
     * @param resourceName the name of the resource on the classpath
     * @param outputName   the filename to copy to (within the tmp dir)
     * @return the extracted file
     * @throws IOException
     */
    File extractResource(final String resourceName, final String outputName) throws JNILibraryLoaderException
    {
        try
        {
            prepareExtraction();

            final InputStream in = this.getClass().getClassLoader().getResourceAsStream(resourceName);
            if (in == null)
            {
                throw new JNILibraryLoaderException("Could not find resource " + resourceName + " on class-path");
            }

            final File outfile = new File(_extractionDirectory, outputName);

            if (outfile.exists())
            {
                _log.warn("Output file for resource '" + resourceName + "' located at '" + outfile.getAbsolutePath() + "' already exists");

                if (_overwrite)
                {
                    _log.warn("Will proceed to overwrite existing resource");
                }
                else
                {
                    _log.info("Will proceed and re-use already extracted resource");
                    return outfile;
                }

            }

            if (!_leaveExtracted)
            {
                _log.debug("Extracted file will be deleted on virtual machine shutdown");
                outfile.deleteOnExit();
            }

            if (_log.isDebugEnabled())
            {
                _log.debug("Extracting '" + resourceName + "' to '" + outfile.getAbsolutePath() + "'");
            }

            final long bytes;
            final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outfile));
            try
            {
                bytes = JNILibraryLoader.copyStream(in, out);
                _log.debug("Resource extraction completed, " + bytes + " bytes successfully read");
                return outfile;
            }
            finally
            {
                out.close();
                in.close();
            }

        }
        catch (Exception e)
        {
            _log.error("Extraction failed: " + e.getMessage(), e);
            throw new JNILibraryLoaderException("Could not extract resource " + resourceName + " Exception: " + e.getMessage(), e);
        }
    }

    /**
     * Copies the content of the specified input stream to the output stream.
     *
     * @param inputStream  The input stream to read from
     * @param outputStream The output stream to write to
     * @return The number of bytes copied
     * @throws IOException If the copy operation fails
     */
    public static long copyStream(final InputStream inputStream, final OutputStream outputStream) throws IOException
    {
        final int size = 8192;
        final byte[] buffer = new byte[size];
        int len = 0;

        long totalCount = 0;
        while ((len = inputStream.read(buffer, 0, size)) != -1)
        {
            outputStream.write(buffer, 0, len);
            totalCount += len;

        }
        outputStream.flush();

        return totalCount;

    }

    public void prepareExtraction() throws Exception
    {
        if (_extractionDirectoryStr.trim().equals(""))
        {
            _extractionDirectoryStr = System.getProperty("java.io.tmpdir");
        }

        _log.info("Extraction Directory: " + _extractionDirectoryStr);
        _log.info("Leave Extracted Resources: " + (_leaveExtracted ? "Enabled" : "Disabled"));
        _log.info("Overwrite Existing Resources: " + (_overwrite ? "Enabled" : "Disabled"));

        _extractionDirectory = new File(_extractionDirectoryStr);
        if (!_extractionDirectory.exists())
        {
            if (_log.isDebugEnabled())
            {
                _log.debug("Extraction directory '" + _extractionDirectory.getAbsolutePath() + "' does not exist, attempting to create");
            }

            if (!_extractionDirectory.mkdirs())
            {
                throw new JNILibraryLoaderException("Could not create extraction directory " + _extractionDirectory);
            }
        }

        if (!_extractionDirectory.canWrite())
        {
            _log.error("No write permission for extraction directory '" + _extractionDirectory.getAbsolutePath() + "'");
            throw new JNILibraryLoaderException("No write permission on the extraction directory " + _extractionDirectory);
        }

    }

    public void setLeaveExtracted(final boolean leaveExtracted)
    {
        _leaveExtracted = leaveExtracted;
    }

    public void setExtractionDirectory(final String extractionDirectory)
    {
        _extractionDirectoryStr = extractionDirectory;
    }

    public void setOverwrite(final boolean overwrite)
    {
        _overwrite = overwrite;
    }
}


