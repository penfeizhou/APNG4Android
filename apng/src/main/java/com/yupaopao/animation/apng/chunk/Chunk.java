package com.yupaopao.animation.apng.chunk;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: Length (长度)	4字节	指定数据块中数据域的长度，其长度不超过(231－1)字节
 * Chunk Type Code (数据块类型码)	4字节	数据块类型码由ASCII字母(A-Z和a-z)组成
 * Chunk Data (数据块数据)	可变长度	存储按照Chunk Type Code指定的数据
 * CRC (循环冗余检测)	4字节	存储用来检测是否有错误的循环冗余码
 * @Link https://www.w3.org/TR/PNG
 * @Author: pengfei.zhou
 * @CreateDate: 2019/3/27
 */
class Chunk {
    int length;
    String typeCode;
    byte[] data;
    int crc;
    private static byte[] __intBytes = new byte[4];

    static Chunk read(InputStream inputStream) {
        Chunk chunk;
        try {
            int length = readIntFromInputStream(inputStream);
            String typeCode = readTypeCodeFromInputStream(inputStream);
            chunk = newInstance(typeCode);
            chunk.typeCode = typeCode;
            chunk.length = length;
            chunk.data = new byte[chunk.length];
            inputStream.read(chunk.data);
            chunk.crc = readIntFromInputStream(inputStream);
            return chunk;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Chunk newInstance(String typeCode) {
        Chunk chunk;
        switch (typeCode) {
            case IHDRChunk.ID:
                chunk = new IHDRChunk();
                break;
            case PLTEChunk.ID:
                chunk = new PLTEChunk();
            case IDATChunk.ID:
                chunk = new IDATChunk();
                break;
            case IENDChunk.ID:
                chunk = new IENDChunk();
                break;
            case ACTLChunk.ID:
                chunk = new ACTLChunk();
                break;
            case FCTLChunk.ID:
                chunk = new FCTLChunk();
                break;
            case FDATChunk.ID:
                chunk = new FDATChunk();
                break;
            case CHRMChunk.ID:
                chunk = new CHRMChunk();
                break;
            case GAMAChunk.ID:
                chunk = new GAMAChunk();
                break;
            case ICCPChunk.ID:
                chunk = new ICCPChunk();
                break;
            case SBITChunk.ID:
                chunk = new SBITChunk();
                break;
            case SRGBChunk.ID:
                chunk = new SRGBChunk();
                break;
            case BKGDChunk.ID:
                chunk = new BKGDChunk();
                break;
            case HISTChunk.ID:
                chunk = new HISTChunk();
                break;
            case TRNSChunk.ID:
                chunk = new TRNSChunk();
                break;
            case PHYSChunk.ID:
                chunk = new PHYSChunk();
                break;
            case SPLTChunk.ID:
                chunk = new SPLTChunk();
                break;
            case TIMEChunk.ID:
                chunk = new TIMEChunk();
                break;
            case ITXTChunk.ID:
                chunk = new ITXTChunk();
                break;
            case TEXTChunk.ID:
                chunk = new TEXTChunk();
                break;
            case ZTXTChunk.ID:
                chunk = new ZTXTChunk();
                break;
            default:
                chunk = new Chunk();
                break;
        }
        return chunk;
    }

    private static int readIntFromInputStream(InputStream inputStream) throws IOException {
        inputStream.read(__intBytes);
        return byteArrayToInt(__intBytes);
    }

    private static String readTypeCodeFromInputStream(InputStream inputStream) throws IOException {
        inputStream.read(__intBytes);
        return new String(__intBytes);
    }


    private static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }
}
