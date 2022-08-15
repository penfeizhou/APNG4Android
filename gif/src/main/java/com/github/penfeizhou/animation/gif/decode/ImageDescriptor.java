package com.github.penfeizhou.animation.gif.decode;

import com.github.penfeizhou.animation.gif.io.GifReader;

import java.io.IOException;

/**
 * Image Descriptor.
 * a. Description. Each image in the Data Stream is composed of an Image
 * Descriptor, an optional Local Color Table, and the image data.  Each
 * image must fit within the boundaries of the Logical Screen, as defined
 * in the Logical Screen Descriptor.
 * <p>
 * The Image Descriptor contains the parameters necessary to process a table
 * based image. The coordinates given in this block refer to coordinates
 * within the Logical Screen, and are given in pixels. This block is a
 * Graphic-Rendering Block, optionally preceded by one or more Control
 * blocks such as the Graphic Control Extension, and may be optionally
 * followed by a Local Color Table; the Image Descriptor is always followed
 * by the image data.
 * <p>
 * This block is REQUIRED for an image.  Exactly one Image Descriptor must
 * be present per image in the Data Stream.  An unlimited number of images
 * may be present per Data Stream.
 * <p>
 * b. Required Version.  87a.
 * <p>
 * c. Syntax.
 * <p>
 * 7 6 5 4 3 2 1 0        Field Name                    Type
 * +---------------+
 * 0  |               |       Image Separator               Byte
 * +---------------+
 * 1  |               |       Image Left Position           Unsigned
 * +-             -+
 * 2  |               |
 * +---------------+
 * 3  |               |       Image Top Position            Unsigned
 * +-             -+
 * 4  |               |
 * +---------------+
 * 5  |               |       Image Width                   Unsigned
 * +-             -+
 * 6  |               |
 * +---------------+
 * 7  |               |       Image Height                  Unsigned
 * +-             -+
 * 8  |               |
 * +---------------+
 * 9  | | | |   |     |       <Packed Fields>               See below
 * +---------------+
 * <p>
 * <Packed Fields>  =      Local Color Table Flag        1 Bit
 * Interlace Flag                1 Bit
 * Sort Flag                     1 Bit
 * Reserved                      2 Bits
 * Size of Local Color Table     3 Bits
 * <p>
 * i) Image Separator - Identifies the beginning of an Image
 * Descriptor. This field contains the fixed value 0x2C.
 * <p>
 * ii) Image Left Position - Column number, in pixels, of the left edge
 * of the image, with respect to the left edge of the Logical Screen.
 * Leftmost column of the Logical Screen is 0.
 * <p>
 * iii) Image Top Position - Row number, in pixels, of the top edge of
 * the image with respect to the top edge of the Logical Screen. Top
 * row of the Logical Screen is 0.
 * <p>
 * iv) Image Width - Width of the image in pixels.
 * <p>
 * v) Image Height - Height of the image in pixels.
 * <p>
 * vi) Local Color Table Flag - Indicates the presence of a Local Color
 * Table immediately following this Image Descriptor. (This field is
 * the most significant bit of the byte.)
 * <p>
 * <p>
 * Values :    0 -   Local Color Table is not present. Use
 * Global Color Table if available.
 * 1 -   Local Color Table present, and to follow
 * immediately after this Image Descriptor.
 * <p>
 * vii) Interlace Flag - Indicates if the image is interlaced. An image
 * is interlaced in a four-pass interlace pattern; see Appendix E for
 * details.
 * <p>
 * Values :    0 - Image is not interlaced.
 * 1 - Image is interlaced.
 * <p>
 * viii) Sort Flag - Indicates whether the Local Color Table is
 * sorted.  If the flag is set, the Local Color Table is sorted, in
 * order of decreasing importance. Typically, the order would be
 * decreasing frequency, with most frequent color first. This assists
 * a decoder, with fewer available colors, in choosing the best subset
 * of colors; the decoder may use an initial segment of the table to
 * render the graphic.
 * <p>
 * Values :    0 -   Not ordered.
 * 1 -   Ordered by decreasing importance, most
 * important color first.
 * <p>
 * ix) Size of Local Color Table - If the Local Color Table Flag is
 * set to 1, the value in this field is used to calculate the number
 * of bytes contained in the Local Color Table. To determine that
 * actual size of the color table, raise 2 to the value of the field
 * + 1. This value should be 0 if there is no Local Color Table
 * specified. (This field is made up of the 3 least significant bits
 * of the byte.)
 * <p>
 * d. Extensions and Scope. The scope of this block is the Table-based Image
 * Data Block that follows it. This block may be modified by the Graphic
 * Control Extension.
 * <p>
 * e. Recommendation. None.
 *
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 */
public class ImageDescriptor implements Block {
    public int frameX;
    public int frameY;
    public int frameWidth;
    public int frameHeight;
    private byte flag;
    public ColorTable localColorTable;
    public int lzwMinimumCodeSize;
    public int imageDataOffset;

    @Override
    public void receive(GifReader reader) throws IOException {
        this.frameX = reader.readUInt16();
        this.frameY = reader.readUInt16();
        this.frameWidth = reader.readUInt16();
        this.frameHeight = reader.readUInt16();
        this.flag = reader.peek();
        if (localColorTableFlag()) {
            this.localColorTable = new ColorTable(localColorTableSize());
            this.localColorTable.receive(reader);
        }
        this.lzwMinimumCodeSize = reader.peek() & 0xff;
        imageDataOffset = reader.position();
        byte blockSize;
        while ((blockSize = reader.peek()) != 0x0) {
            reader.skip(blockSize & 0xff);
        }
    }

    public boolean localColorTableFlag() {
        return (this.flag & 0x80) == 0x80;
    }

    public boolean interlaceFlag() {
        return (this.flag & 0x40) == 0x40;
    }

    public boolean sortFlag() {
        return (this.flag & 0x20) == 0x20;
    }

    public int localColorTableSize() {
        return 2 << (this.flag & 0xf);
    }

    @Override
    public int size() {
        return 0;
    }
}
