package com.github.penfeizhou.animation.gif.decode;

import com.github.penfeizhou.animation.gif.io.GifReader;

import java.io.IOException;

/**
 * @Description: GraphicControlExtension
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-17
 */
public class GraphicControlExtension extends ExtensionBlock {
    private int blockSize;
    private byte packedFields;
    public int delayTime;
    public int transparentColorIndex;

    @Override
    public void receive(GifReader reader) throws IOException {
        blockSize = reader.peek() & 0xff;
        packedFields = reader.peek();
        delayTime = reader.readUInt16();
        transparentColorIndex = reader.peek() & 0xff;
        if (reader.peek() != 0) {
            throw new GifParser.FormatException();
        }
    }

    /**
     * Values :
     * 0 -   No disposal specified. The decoder is
     * not required to take any action.
     * 1 -   Do not dispose. The graphic is to be left
     * in place.
     * 2 -   Restore to background color. The area used by the
     * graphic must be restored to the background color.
     * 3 -   Restore to previous. The decoder is required to
     * restore the area overwritten by the graphic with
     * what was there prior to rendering the graphic.
     * 4-7 -    To be defined.
     */
    public int disposalMethod() {
        return packedFields >> 2 & 0x7;
    }

    /**
     * User Input Flag - Indicates whether or not user input is
     * expected before continuing. If the flag is set, processing will
     * continue when user input is entered. The nature of the User input
     * is determined by the application (Carriage Return, Mouse Button
     * Click, etc.).
     * <p>
     * Values :
     * 0 -   User input is not expected.
     * 1 -   User input is expected.
     * <p>
     * When a Delay Time is used and the User Input Flag is set,
     * processing will continue when user input is received or when the
     * delay time expires, whichever occurs first.
     */
    public boolean userInputFlag() {
        return (packedFields & 0x2) == 0x2;
    }

    /**
     * When a Delay Time is used and the User Input Flag is set,
     * processing will continue when user input is received or when the
     * delay time expires, whichever occurs first.
     * <p>
     * vi) Transparency Flag - Indicates whether a transparency index is
     * given in the Transparent Index field. (This field is the least
     * significant bit of the byte.)
     * <p>
     * Values :
     * 0 -   Transparent Index is not given.
     * 1 -   Transparent Index is given.
     */
    public boolean transparencyFlag() {
        return (packedFields & 0x1) == 0x1;
    }

    @Override
    public int size() {
        return blockSize + 1;
    }
}
