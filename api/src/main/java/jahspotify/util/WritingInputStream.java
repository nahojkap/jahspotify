package jahspotify.util;

import java.io.*;

public class WritingInputStream extends FilterInputStream
{
    private OutputStream _outputStream;

    /**
     * Creates a <code>FilterInputStream</code>
     * by assigning the  argument <code>in</code>
     * to the field <code>this.in</code> so as
     * to remember it for later use.
     *
     * @param inputStream the underlying input stream, or <code>null</code> if
     *                    this instance is to be created without an underlying stream.
     */
    public WritingInputStream(InputStream inputStream, OutputStream outputStream)
    {
        super(inputStream);
        _outputStream = outputStream;
    }

    @Override
    public int read() throws IOException
    {
        final int read = super.read();
        if (read != -1)
        {
            _outputStream.write(read);
        }
        return read;
    }

    @Override
    public int read(final byte[] b) throws IOException
    {
        final int read = super.read(b);
        if (read != -1)
        {
            _outputStream.write(b, 0, read);
        }
        return read;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException
    {
        final int read = super.read(b, off, len);
        if (read != -1)
        {
            _outputStream.write(b, off, read);
        }
        return read;
    }

    @Override
    public void close() throws IOException
    {
        _outputStream.flush();
        _outputStream.close();
        super.close();
    }
}