package com.github.penfeizhou.animation.gif.decode;

import com.github.penfeizhou.animation.gif.io.GifReader;

import java.io.IOException;

/**
 * Global Color Table.
 * <p>
 * a. Description. This block contains a color table, which is a sequence of
 * bytes representing red-green-blue color triplets. The Global Color Table
 * is used by images without a Local Color Table and by Plain Text
 * Extensions. Its presence is marked by the Global Color Table Flag being
 * set to 1 in the Logical Screen Descriptor; if present, it immediately
 * follows the Logical Screen Descriptor and contains a number of bytes
 * equal to
 * 3 x 2^(Size of Global Color Table+1).
 * <p>
 * This block is OPTIONAL; at most one Global Color Table may be present
 * per Data Stream.
 * <p>
 * b. Required Version.  87a
 * <p>
 * <p>
 * c. Syntax.
 * <p>
 * 7 6 5 4 3 2 1 0        Field Name                    Type
 * +===============+
 * 0  |               |       Red 0                         Byte
 * +-             -+
 * 1  |               |       Green 0                       Byte
 * +-             -+
 * 2  |               |       Blue 0                        Byte
 * +-             -+
 * 3  |               |       Red 1                         Byte
 * +-             -+
 * |               |       Green 1                       Byte
 * +-             -+
 * up  |               |
 * +-   . . . .   -+       ...
 * to  |               |
 * +-             -+
 * |               |       Green 255                     Byte
 * +-             -+
 * 767  |               |       Blue 255                      Byte
 * +===============+
 * <p>
 * <p>
 * d. Extensions and Scope. The scope of this block is the entire Data
 * Stream. This block cannot be modified by any extension.
 * <p>
 * e. Recommendation. None.
 *
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 */
public class ColorTable implements Block {
    private int[] colorTable;

    public ColorTable(int tableSize) {
        this.colorTable = new int[tableSize];
    }

    @Override
    public void receive(GifReader reader) throws IOException {
        for (int i = 0; i < this.colorTable.length; i++) {
            byte red = reader.peek();
            byte green = reader.peek();
            byte blue = reader.peek();
            // store in RGBA order,in case use for byte buffer
            this.colorTable[i] = 0xff000000 | ((blue & 0xff) << 16) | ((green & 0xff) << 8) | (red & 0xff);
        }
    }

    public int[] getColorTable() {
        return colorTable;
    }

    @Override
    public int size() {
        return this.colorTable.length * 3;
    }
}
