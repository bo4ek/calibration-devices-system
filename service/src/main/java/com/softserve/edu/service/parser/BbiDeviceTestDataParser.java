package com.softserve.edu.service.parser;

import com.softserve.edu.common.Constants;
import com.softserve.edu.device.test.data.BbiDeviceTestData;
import com.softserve.edu.device.test.data.DeviceTestData;
import com.softserve.edu.service.exceptions.InvalidImageInBbiException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BbiDeviceTestDataParser implements DeviceTestDataParser {
    private InputStream reader;
    private Map<String, Object> resultMap;

    private final Logger logger = Logger.getLogger(BbiDeviceTestDataParser.class);

    @Override
    public DeviceTestData parse(InputStream deviceTestDataStream) throws IOException, DecoderException, InvalidImageInBbiException {
        resultMap = new HashMap<>();
        reader = new BufferedInputStream(deviceTestDataStream);
        long count;

        resultMap.put("day", readLongValueReversed(Constants.ONE_BYTE)); //0x800000
        resultMap.put("month", readLongValueReversed(Constants.ONE_BYTE)); //0x800001
        resultMap.put("year", readLongValueReversed(Constants.TWO_BYTES)); //0x800002 + 0x02
        resultMap.put("hour", readLongValueReversed(Constants.ONE_BYTE)); //0x800004
        resultMap.put("minute", readLongValueReversed(Constants.ONE_BYTE)); //0x800005
        resultMap.put("second", readLongValueReversed(Constants.ONE_BYTE)); //0x800006
        resultMap.put("dayOfWeek", readLongValueReversed(Constants.ONE_BYTE)); //0x800007
        resultMap.put("unixTime", readLongValueReversed(Constants.FOUR_BYTES) * 1000); //0x800008 + 0x04
        resultMap.put("regStart", readConsecutiveBytesReversed(Constants.FOUR_BYTES)); //0x80000c + 0x04
        count = reader.skip(Constants.FOUR_BYTES); //0x800010 + 0x04
        resultMap.put("temperature", readLongValueReversed(Constants.FOUR_BYTES)); //0x800014 + 0x04
        resultMap.put("batteryCharge", readLongValueReversed(Constants.FOUR_BYTES)); //0x800018 + 0x04
        resultMap.put("bbiWritten", readLongValueReversed(Constants.FOUR_BYTES)); //0x80001c + 0x04
        resultMap.put("bbiAvailableToWrite", readLongValueReversed(Constants.FOUR_BYTES)); //0x800020 + 0x04
        resultMap.put("fileName", readLongValueReversed(Constants.FOUR_BYTES)); //0x800024 + 0x04
        count = reader.skip(Constants.TWELVE_BYTES); //0x800028 + 0x0c
        resultMap.put("integrationTime", readLongValueReversed(Constants.FOUR_BYTES)); //0x800034 + 0x04
        resultMap.put("testCounter", readLongValueReversed(Constants.FOUR_BYTES)); //0x800038 + 0x04
        resultMap.put("confirmationRegister", readLongValueReversed(Constants.FOUR_BYTES)); //0x80003c + 0x04
        resultMap.put("regControl", readConsecutiveBytesReversed(Constants.FOUR_BYTES)); //0x800040 + 0x04
        resultMap.put("installmentNumber", readLongValueReversed(Constants.FOUR_BYTES)); //0x800044 + 0x04
        resultMap.put("currentCounterNumber", readConsecutiveBytesAsUTF8(Constants.TWELVE_BYTES));
        resultMap.put("latitude", readLongValueReversed(Constants.FOUR_BYTES) / 100000.0);  //0x800054+0x04
        resultMap.put("longitude", readLongValueReversed(Constants.FOUR_BYTES) / 100000.0); //0x800058+0x04
        resultMap.put("impulsePricePerLitre", readLongValueReversed(Constants.FOUR_BYTES)); //0x80005c+0x04
        resultMap.put("initialCapacity", readConsecutiveBytesAsUTF8(Constants.EIGHT_BYTES)); //0x800060+0x08
        resultMap.put("counterType1", readConsecutiveBytesAsUTF8(Constants.SIXTEEN_BYTES)); //0x800068+0x10
        resultMap.put("testimony", readLongValueReversed(Constants.FOUR_BYTES)); //0x800078+0x04
        resultMap.put("counterProductionYear", readLongValueReversed(Constants.FOUR_BYTES)); //0x80007c+0x04
        resultMap.put("counterType2", readConsecutiveBytesAsUTF8(Constants.SIXTEEN_BYTES)); //0x800080+0x10
        resultMap.put("fileOpened", readLongValueReversed(Constants.FOUR_BYTES)); //0x800090+0x04
        resultMap.put("deviceTypeId", readLongValueReversed(Constants.FOUR_BYTES)); // 0x800094+0x04 DeviceType WATER(1), THERMAL(2), ELECTRICAL(3), GASEOUS(4)
        count = reader.skip(Constants.SKIP_TO_TESTS); //0x800100 now
        for (int i = 1; i <= Constants.TEST_COUNT; ++i) {
            readTest(i);
            if (i != Constants.TEST_COUNT) {
                count = reader.skip(Constants.EMPTY_BYTES_BETWEEN_TESTS);
            }
        }
        resultMap.put("fullInstallmentNumber", readConsecutiveBytesAsUTF8(Constants.THIRTY_TWO_BYTES)); //0x0x80064c+32
        count = reader.skip(Constants.SKIP_TO_IMAGES); //go to images

        resultMap.put("testPhoto", readImageBase64());
        for (int i = 0; i < 12; ++i) {
            String imageKey = "test" + (i / 2 + 1) + (i % 2 == 0 ? "begin" : "end") + "Photo";
            resultMap.put(imageKey, readImageBase64());
        }
        return new BbiDeviceTestData(resultMap);
    }

    /**
     * Reads specified amount of bytes from InputStream reader and
     * concatenates them in reversed order.
     *
     * @param bytesAmount amount of bytes to read.
     * @return string which contains concatenated bytes in reverse order.
     * @throws IOException
     */
    private String readConsecutiveBytesReversed(int bytesAmount) throws IOException {
        byte[] byteArray = new byte[bytesAmount];
        reader.read(byteArray, Constants.START_OFFSET_IN_ARRAY, bytesAmount);
        ArrayUtils.reverse(byteArray);
        return new String(byteArray, Constants.UTF8_ENCODING);
    }

    /**
     * Reads specified amount of bytes from InputStream reader
     * and converts them into UTF8 string.
     *
     * @param bytesAmount amount of bytes to read.
     * @return string which contains UTF8 symbols.
     * @throws IOException
     */
    private String readConsecutiveBytesAsUTF8(int bytesAmount) throws IOException {
        byte[] byteArray = new byte[bytesAmount];
        reader.read(byteArray, Constants.START_OFFSET_IN_ARRAY, bytesAmount);
        return new String(byteArray, Constants.UTF8_ENCODING);
    }

    /**
     * Reads specified amount of bytes from InputStream reader
     * into a long variable.
     *
     * @param bytesAmount amount of bytes to read.
     * @return long value.
     * @throws IOException
     */
    private long readLongValue(int bytesAmount) throws IOException {
        long result = 0;
        for (int i = 0; i < bytesAmount; ++i) {
            result <<= 8;
            result += reader.read();
        }
        return result;
    }

    /**
     * Reads specified amount of bytes from InputStream reader
     * in reverse order into a long variable.
     *
     * @param bytesAmount amount of bytes to read.
     * @return long value.
     * @throws IOException
     */
    private long readLongValueReversed(int bytesAmount) throws IOException {
        long result = 0;
        for (int i = 0; i < bytesAmount; ++i) {
            result += reader.read() << 8 * i;
        }
        return result;
    }

    private void readTest(int testIndex) throws IOException {
        resultMap.put("test" + testIndex + "specifiedConsumption", readLongValueReversed(Constants.FOUR_BYTES)); //0x800100+0x04
        resultMap.put("test" + testIndex + "lowerConsumptionLimit", readLongValueReversed(Constants.FOUR_BYTES)); //0x800104+0x04
        resultMap.put("test" + testIndex + "upperConsumptionLimit", readLongValueReversed(Constants.FOUR_BYTES)); //0x800108+0x04
        resultMap.put("test" + testIndex + "allowableError", readLongValueReversed(Constants.FOUR_BYTES) / 10); //0x80010с+0x04
        resultMap.put("test" + testIndex + "specifiedImpulsesAmount", readLongValueReversed(Constants.FOUR_BYTES) / 10000.0); //0x800110+0x04
        resultMap.put("test" + testIndex + "correctedCumulativeImpulsesValue", readLongValueReversed(Constants.FOUR_BYTES) / Constants.THOUSAND); //0x800114+0x04
        resultMap.put("test" + testIndex + "correctedCurrentConsumption", readLongValueReversed(Constants.FOUR_BYTES) / Constants.THOUSAND); //0x800118+0x04
        resultMap.put("test" + testIndex + "cumulativeImpulsesValueWithoutCorrection", readLongValueReversed(Constants.FOUR_BYTES) / Constants.THOUSAND); //0x80011с+0x04
        resultMap.put("test" + testIndex + "currentConsumptionWithoutCorrection", readLongValueReversed(Constants.FOUR_BYTES) / Constants.THOUSAND); //0x800120+0x04
        resultMap.put("test" + testIndex + "estimatedError", readLongValueReversed(Constants.FOUR_BYTES)); //0x800124+0x04
        resultMap.put("test" + testIndex + "initialCounterValue", readLongValueReversed(Constants.FOUR_BYTES) / 10000.0); //0x800128+0x04
        resultMap.put("test" + testIndex + "terminalCounterValue", readLongValueReversed(Constants.FOUR_BYTES) / 10000.0); //0x80012с+0x04
        resultMap.put("test" + testIndex + "unixTestBeginTime", readLongValueReversed(Constants.FOUR_BYTES)); //0x800130+0x04
        resultMap.put("test" + testIndex + "unixTestEndTime", readLongValueReversed(Constants.FOUR_BYTES)); //0x800134+0x04
        resultMap.put("test" + testIndex + "testDuration", readLongValueReversed(Constants.FOUR_BYTES) / Constants.THOUSAND); //0x800138+0x04
        resultMap.put("test" + testIndex + "correctionFactor", readLongValueReversed(Constants.FOUR_BYTES)); //0x80013с+0x04
        resultMap.put("test" + testIndex + "minConsumptionLimit", readLongValueReversed(Constants.FOUR_BYTES)); //0x800140+0x04
        resultMap.put("test" + testIndex + "maxConsumptionLimit", readLongValueReversed(Constants.FOUR_BYTES)); //0x800144+0x04
        resultMap.put("test" + testIndex + "testNumber", readLongValueReversed(Constants.FOUR_BYTES)); //0x800148+0x04
    }

    /**
     * Reads image size. Then reads next imageSize bytes and
     * converts byteArray to String base64 variable.
     * Finally, skips all blank bytes reserved for image.
     *
     * @return Image written in base64 string.
     * @throws IOException
     */
    public String readImageBase64() throws IOException, DecoderException,InvalidImageInBbiException {
        String encodedHexB64;

        try {
            int imageSize = (int) readLongValue(Constants.FOUR_BYTES);
            byte[] decodedHex = new byte[imageSize];
            reader.read(decodedHex, Constants.START_OFFSET_IN_ARRAY, imageSize);
            encodedHexB64 = Base64.encodeBase64String(decodedHex);

            // skips all empty bytes till the next image beginning.
            long count = reader.skip(Constants.ALLOCATED_IMAGE_SIZE - imageSize);
            return encodedHexB64;
        }catch (Exception e){
            throw new InvalidImageInBbiException();
        }
    }
}