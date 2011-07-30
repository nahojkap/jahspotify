package jahspotify.util;

import java.io.*;

public class DuplicatingOutputStream extends FilterOutputStream
{
    private OutputStream[] _outputStreams;

    /**
     * Creates an output stream filter built on top of the specified
     * underlying output stream.
     *
     * @param out the underlying output stream to be assigned to
     *            the field <tt>this.out</tt> for later use, or
     *            <code>null</code> if this instance is to be
     *            created without an underlying stream.
     */
    public DuplicatingOutputStream(OutputStream out, OutputStream... outputStreams)
    {
        super(out);
        _outputStreams = (outputStreams == null ? new OutputStream[9] : outputStreams);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException
    {
        super.write(b, off, len);
        for (OutputStream outputStream : _outputStreams)
        {
            outputStream.write(b, off, len);
        }
    }

    @Override
    public void write(final int b) throws IOException
    {
        super.write(b);
        for (OutputStream outputStream : _outputStreams)
        {
            outputStream.write(b);
        }
    }

    @Override
    public void write(final byte[] b) throws IOException
    {
        super.write(b);
        for (OutputStream outputStream : _outputStreams)
        {
            outputStream.write(b);
        }
    }

    @Override
    public void close() throws IOException
    {
        super.close();
        for (OutputStream outputStream : _outputStreams)
        {
            outputStream.close();
        }
    }

    @Override
    public void flush() throws IOException
    {
        super.flush();
        for (OutputStream outputStream : _outputStreams)
        {
            outputStream.flush();
        }
    }
}