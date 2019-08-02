package com.github.penfeizhou.animation.gif.decode;

import com.github.penfeizhou.animation.gif.io.GifReader;

import java.io.IOException;

/**
 * Logical Screen Descriptor.
 * <p>
 * a. Description.  The Logical Screen Descriptor contains the parameters
 * necessary to define the area of the display device within which the
 * images will be rendered.  The coordinates in this block are given with
 * respect to the top-left corner of the virtual screen; they do not
 * necessarily refer to absolute coordinates on the display device.  This
 * implies that they could refer to window coordinates in a window-based
 * environment or printer coordinates when a printer is used.
 * <p>
 * This block is REQUIRED; exactly one Logical Screen Descriptor must be
 * present per Data Stream.
 * <p>
 * b. Required Version.  Not applicable. This block is not subject to a
 * version number. This block must appear immediately after the Header.
 * <p>
 * c. Syntax.
 * <p>
 * 7 6 5 4 3 2 1 0        Field Name                    Type
 * +---------------+
 * 0  |               |       Logical Screen Width          Unsigned
 * +-             -+
 * 1  |               |
 * +---------------+
 * 2  |               |       Logical Screen Height         Unsigned
 * +-             -+
 * 3  |               |
 * +---------------+
 * 4  | |     | |     |       <Packed Fields>               See below
 * +---------------+
 * 5  |               |       Background Color Index        Byte
 * +---------------+
 * 6  |               |       Pixel Aspect Ratio            Byte
 * +---------------+
 * <Packed Fields>  =
 * Global Color Table Flag       1 Bit
 * Color Resolution              3 Bits
 * Sort Flag                     1 Bit
 * Size of Global Color Table    3 Bits
 * <p>
 * i) Logical Screen Width - Width, in pixels, of the Logical Screen
 * where the images will be rendered in the displaying device.
 * <p>
 * ii) Logical Screen Height - Height, in pixels, of the Logical
 * Screen where the images will be rendered in the displaying device.
 * <p>
 * iii) Global Color Table Flag - Flag indicating the presence of a
 * Global Color Table; if the flag is set, the Global Color Table will
 * immediately follow the Logical Screen Descriptor. This flag also
 * selects the interpretation of the Background Color Index; if the
 * flag is set, the value of the Background Color Index field should
 * be used as the table index of the background color. (This field is
 * the most significant bit of the byte.)
 * <p>
 * Values :    0 -   No Global Color Table follows, the Background
 * Color Index field is meaningless.
 * 1 -   A Global Color Table will immediately follow, the
 * Background Color Index field is meaningful.
 * <p>
 * iv) Color Resolution - Number of bits per primary color available
 * to the original image, minus 1. This value represents the size of
 * the entire palette from which the colors in the graphic were
 * selected, not the number of colors actually used in the graphic.
 * For example, if the value in this field is 3, then the palette of
 * the original image had 4 bits per primary color available to create
 * the image.  This value should be set to indicate the richness of
 * the original palette, even if not every color from the whole
 * palette is available on the source machine.
 * <p>
 * v) Sort Flag - Indicates whether the Global Color Table is sorted.
 * If the flag is set, the Global Color Table is sorted, in order of
 * decreasing importance. Typically, the order would be decreasing
 * frequency, with most frequent color first. This assists a decoder,
 * with fewer available colors, in choosing the best subset of colors;
 * the decoder may use an initial segment of the table to render the
 * graphic.
 * <p>
 * Values :    0 -   Not ordered.
 * 1 -   Ordered by decreasing importance, most
 * important color first.
 * <p>
 * vi) Size of Global Color Table - If the Global Color Table Flag is
 * set to 1, the value in this field is used to calculate the number
 * of bytes contained in the Global Color Table. To determine that
 * actual size of the color table, raise 2 to [the value of the field
 * + 1].  Even if there is no Global Color Table specified, set this
 * field according to the above formula so that decoders can choose
 * the best graphics mode to display the stream in.  (This field is
 * made up of the 3 least significant bits of the byte.)
 * <p>
 * vii) Background Color Index - Index into the Global Color Table for
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * 10
 * <p>
 * <p>
 * the Background Color. The Background Color is the color used for
 * those pixels on the screen that are not covered by an image. If the
 * Global Color Table Flag is set to (zero), this field should be zero
 * and should be ignored.
 * <p>
 * viii) Pixel Aspect Ratio - Factor used to compute an approximation
 * of the aspect ratio of the pixel in the original image.  If the
 * value of the field is not 0, this approximation of the aspect ratio
 * is computed based on the formula:
 * <p>
 * Aspect Ratio = (Pixel Aspect Ratio + 15) / 64
 * <p>
 * The Pixel Aspect Ratio is defined to be the quotient of the pixel's
 * width over its height.  The value range in this field allows
 * specification of the widest pixel of 4:1 to the tallest pixel of
 * 1:4 in increments of 1/64th.
 * <p>
 * Values :        0 -   No aspect ratio information is given.
 * 1..255 -   Value used in the computation.
 * <p>
 * d. Extensions and Scope. The scope of this block is the entire Data
 * Stream. This block cannot be modified by any extension.
 * <p>
 * e. Recommendations. None.
 *
 * @Author: pengfei.zhou
 * @CreateDate: 2019-05-16
 */
public class LogicalScreenDescriptor implements Block {
    public int screenWidth;
    public int screenHeight;
    byte flag;
    public byte bgColorIndex;
    byte radio;

    @Override
    public void receive(GifReader reader) throws IOException {
        screenWidth = reader.readUInt16();
        screenHeight = reader.readUInt16();
        flag = reader.peek();
        bgColorIndex = reader.peek();
        radio = reader.peek();
    }

    @Override
    public int size() {
        return 7;
    }

    public boolean gColorTableFlag() {
        return (flag & 0x80) == 0x80;
    }

    public int colorResolution() {
        return ((flag & 0x70) >> 4) + 1;
    }

    public boolean sortFlag() {
        return (flag & 0x8) == 0x8;
    }

    public int gColorTableSize() {
        return 2 << (flag & 0x7);
    }

}
