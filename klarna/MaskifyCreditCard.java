
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaskifyCreditCard {
    ///*
//
//Mask all digits (0-9) with #, unless they are first or last four characters.
//    Never mask credit cards with less than 6 characters.
//    Never mask non-digit characters.
//
// *//
    public static String maskify(String creditCardNumber) {
        if (creditCardNumber.trim().isEmpty()) {
            return "";
        }
        if (creditCardNumber.contains(" ")){
            throw new IllegalArgumentException("Credit card number should not contain space(s)");
        }
        int creditCardLength = creditCardNumber.length();
        if (creditCardLength < 6) {
            return creditCardNumber;
        }
        StringBuilder maskedStringBuilder = new StringBuilder(creditCardLength);
        maskedStringBuilder.append(creditCardNumber.charAt(0));
        for (int i = 1; i < creditCardLength; i++) {
            char c = creditCardNumber.charAt(i);
            if (Character.isDigit(c)) {
                if (i >= creditCardLength - 4) {
                    maskedStringBuilder.append(c);
                } else {
                    maskedStringBuilder.append("#");
                }
            } else {
                maskedStringBuilder.append(c);
            }
        }
        return maskedStringBuilder.toString();
    }


    @Test
    public void shouldMaskDigitsForBasicCreditCards() {
        assertEquals("5###########0694", maskify("5512103073210694"));
    }

    @Test
    public void shouldNotMaskDigitsForShortCreditCards() {
        assertEquals("54321", maskify("54321"));
    }
}
