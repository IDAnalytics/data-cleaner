// Convert iso8859-15 0x80-0xFF to regular 0x20-0x7F

public class Romanize {
    // Ligatures (indexed from 1)
    private final static char[][] ligature = new char[][] {
    	{'\u0152', '\u004f', '\u0045'},
    	{'\u0153', '\u006f', '\u0065'},
    	{'\u00c6', '\u0041', '\u0045'},
    	{'\u00e6', '\u0061', '\u0065'},
    };

    // Regulars
    private final static char[][] regularInit = new char[][] {
    	{'\u00a0', '\u0020'},
    	{'\u0160', '\u0053'},
    	{'\u0161', '\u0073'},
    	{'\u017d', '\u005a'},
    	{'\u017e', '\u007a'},
    	{'\u0152', '\u0001'},
    	{'\u0153', '\u0002'},
    	{'\u0178', '\u0059'},
    	{'\u00c0', '\u0041'},
    	{'\u00c1', '\u0041'},
    	{'\u00c2', '\u0041'},
    	{'\u00c3', '\u0041'},
    	{'\u00c4', '\u0041'},
    	{'\u00c5', '\u0041'},
    	{'\u00c6', '\u0003'},
    	{'\u00c7', '\u0043'},
    	{'\u00c8', '\u0045'},
    	{'\u00c9', '\u0045'},
    	{'\u00ca', '\u0045'},
    	{'\u00cb', '\u0045'},
    	{'\u00cc', '\u0049'},
    	{'\u00cd', '\u0049'},
    	{'\u00ce', '\u0049'},
    	{'\u00cf', '\u0049'},
    	{'\u00d0', '\u0057'},
    	{'\u00d1', '\u004e'},
    	{'\u00d2', '\u004f'},
    	{'\u00d3', '\u004f'},
    	{'\u00d4', '\u004f'},
    	{'\u00d5', '\u004f'},
    	{'\u00d6', '\u004f'},
    	{'\u00d7', '\u0054'},
    	{'\u00d8', '\u004f'},
    	{'\u00d9', '\u0055'},
    	{'\u00da', '\u0055'},
    	{'\u00db', '\u0055'},
    	{'\u00dc', '\u0055'},
    	{'\u00dd', '\u0059'},
    	{'\u00de', '\u0059'},
    	{'\u00df', '\u0053'},
    	{'\u00e0', '\u0061'},
    	{'\u00e1', '\u0061'},
    	{'\u00e2', '\u0061'},
    	{'\u00e3', '\u0061'},
    	{'\u00e4', '\u0061'},
    	{'\u00e5', '\u0061'},
    	{'\u00e6', '\u0004'},
    	{'\u00e7', '\u0063'},
    	{'\u00e8', '\u0065'},
    	{'\u00e9', '\u0065'},
    	{'\u00ea', '\u0065'},
    	{'\u00eb', '\u0065'},
    	{'\u00ec', '\u0069'},
    	{'\u00ed', '\u0069'},
    	{'\u00ee', '\u0069'},
    	{'\u00ef', '\u0069'},
    	{'\u00f0', '\u0077'},
    	{'\u00f1', '\u006e'},
    	{'\u00f2', '\u006f'},
    	{'\u00f3', '\u006f'},
    	{'\u00f4', '\u006f'},
    	{'\u00f5', '\u006f'},
    	{'\u00f6', '\u006f'},
    	{'\u00f7', '\u0074'},
    	{'\u00f8', '\u006f'},
    	{'\u00f9', '\u0075'},
    	{'\u00fa', '\u0075'},
    	{'\u00fb', '\u0075'},
    	{'\u00fc', '\u0075'},
    	{'\u00fd', '\u0079'},
    	{'\u00fe', '\u0079'},
    	{'\u00ff', '\u0079'},

    	/*
        {(byte)0xa0, 0x20},     // NBSP -> SP
        {(byte)0xa6, 0x53},     // S caron -> S
        {(byte)0xa8, 0x73},     // s caron -> s
        {(byte)0xb4, 0x5a},     // Z caron -> Z
        {(byte)0xb8, 0x7a},     // z caron -> z
        {(byte)0xbc, 0x01},     // OE ligature -> OE
        {(byte)0xbd, 0x02},     // oe ligature -> oe
        {(byte)0xbe, 0x59},     // Y diaeresis -> Y
        {(byte)0xc0, 0x41},     // A grave -> A
        {(byte)0xc1, 0x41},     // A acute -> A
        {(byte)0xc2, 0x41},     // A circumflex -> A
        {(byte)0xc3, 0x41},     // A tilde -> A
        {(byte)0xc4, 0x41},     // A diaeresis -> A
        {(byte)0xc5, 0x41},     // A ring -> A
        {(byte)0xc6, 0x03},     // AE ligature -> AE
        {(byte)0xc7, 0x43},     // C cedilla -> C
        {(byte)0xc8, 0x45},     // E grave -> E
        {(byte)0xc9, 0x45},     // E acute -> E
        {(byte)0xca, 0x45},     // E circumflex -> E
        {(byte)0xcb, 0x45},     // E diaeresis -> E
        {(byte)0xcc, 0x49},     // I grave -> I
        {(byte)0xcd, 0x49},     // I acute -> I
        {(byte)0xce, 0x49},     // I circumflex -> I
        {(byte)0xcf, 0x49},     // I diaeresis -> I
        {(byte)0xd0, 0x57},     // W circumflex -> W
        {(byte)0xd1, 0x4e},     // N tilde -> N
        {(byte)0xd2, 0x4f},     // O grave -> O
        {(byte)0xd3, 0x4f},     // O acute -> O
        {(byte)0xd4, 0x4f},     // O circumflex -> O
        {(byte)0xd5, 0x4f},     // O tilde -> O
        {(byte)0xd6, 0x4f},     // O diaeresis -> O
        {(byte)0xd7, 0x54},     // T dot -> T
        {(byte)0xd8, 0x4f},     // O stroke -> O
        {(byte)0xd9, 0x55},     // U grave -> U
        {(byte)0xda, 0x55},     // U acute -> U
        {(byte)0xdb, 0x55},     // U circumflex -> U
        {(byte)0xdc, 0x55},     // U diaeresis -> U
        {(byte)0xdd, 0x59},     // Y acute -> Y
        {(byte)0xde, 0x59},     // Y circumflex -> Y
        {(byte)0xdf, 0x53},     // S sharp -> S
        {(byte)0xe0, 0x61},     // a grave -> a
        {(byte)0xe1, 0x61},     // a acute -> a
        {(byte)0xe2, 0x61},     // a circumflex -> a
        {(byte)0xe3, 0x61},     // a tilde -> a
        {(byte)0xe4, 0x61},     // a diaeresis -> a
        {(byte)0xe5, 0x61},     // a ring -> a
        {(byte)0xe6, 0x04},     // ae ligature -> ae
        {(byte)0xe7, 0x63},     // c cedilla -> c
        {(byte)0xe8, 0x65},     // e grave -> e
        {(byte)0xe9, 0x65},     // e acute -> e
        {(byte)0xea, 0x65},     // e circumflex -> e
        {(byte)0xeb, 0x65},     // e diaeresis -> e
        {(byte)0xec, 0x69},     // i grave -> i
        {(byte)0xed, 0x69},     // i acute -> i
        {(byte)0xee, 0x69},     // i circumflex -> i
        {(byte)0xef, 0x69},     // i diaeresis -> i
        {(byte)0xf0, 0x77},     // w circumflex -> w
        {(byte)0xf1, 0x6e},     // n tilde -> n
        {(byte)0xf2, 0x6f},     // o grave -> o
        {(byte)0xf3, 0x6f},     // o acute -> o
        {(byte)0xf4, 0x6f},     // o circumflex -> o
        {(byte)0xf5, 0x6f},     // o tilde -> o
        {(byte)0xf6, 0x6f},     // o diaeresis -> o
        {(byte)0xf7, 0x74},     // t dot -> t
        {(byte)0xf8, 0x6f},     // o stroke -> o
        {(byte)0xf9, 0x75},     // u grave -> u
        {(byte)0xfa, 0x75},     // u acute -> u
        {(byte)0xfb, 0x75},     // u circumflex -> u
        {(byte)0xfc, 0x75},     // u diaeresis -> u
        {(byte)0xfd, 0x79},     // y acute -> y
        {(byte)0xfe, 0x79},     // y circumflex -> y
        {(byte)0xff, 0x79}      // y diaeresis -> y
        */
    };

    private final static char[][] regularCapInit = new char[][] {
    	{'\u00a0', '\u0020'},
    	{'\u0160', '\u0053'},
    	{'\u0161', '\u0053'},
    	{'\u017d', '\u005a'},
    	{'\u017e', '\u005a'},
    	{'\u0152', '\u0001'},
    	{'\u0153', '\u0001'},
    	{'\u0178', '\u0059'},
    	{'\u00c0', '\u0041'},
    	{'\u00c1', '\u0041'},
    	{'\u00c2', '\u0041'},
    	{'\u00c3', '\u0041'},
    	{'\u00c4', '\u0041'},
    	{'\u00c5', '\u0041'},
    	{'\u00c6', '\u0003'},
    	{'\u00c7', '\u0043'},
    	{'\u00c8', '\u0045'},
    	{'\u00c9', '\u0045'},
    	{'\u00ca', '\u0045'},
    	{'\u00cb', '\u0045'},
    	{'\u00cc', '\u0049'},
    	{'\u00cd', '\u0049'},
    	{'\u00ce', '\u0049'},
    	{'\u00cf', '\u0049'},
    	{'\u00d0', '\u0057'},
    	{'\u00d1', '\u004e'},
    	{'\u00d2', '\u004f'},
    	{'\u00d3', '\u004f'},
    	{'\u00d4', '\u004f'},
    	{'\u00d5', '\u004f'},
    	{'\u00d6', '\u004f'},
    	{'\u00d7', '\u0054'},
    	{'\u00d8', '\u004f'},
    	{'\u00d9', '\u0055'},
    	{'\u00da', '\u0055'},
    	{'\u00db', '\u0055'},
    	{'\u00dc', '\u0055'},
    	{'\u00dd', '\u0059'},
    	{'\u00de', '\u0059'},
    	{'\u00df', '\u0053'},
    	{'\u00e0', '\u0041'},
    	{'\u00e1', '\u0041'},
    	{'\u00e2', '\u0041'},
    	{'\u00e3', '\u0041'},
    	{'\u00e4', '\u0041'},
    	{'\u00e5', '\u0041'},
    	{'\u00e6', '\u0003'},
    	{'\u00e7', '\u0043'},
    	{'\u00e8', '\u0045'},
    	{'\u00e9', '\u0045'},
    	{'\u00ea', '\u0045'},
    	{'\u00eb', '\u0045'},
    	{'\u00ec', '\u0049'},
    	{'\u00ed', '\u0049'},
    	{'\u00ee', '\u0049'},
    	{'\u00ef', '\u0049'},
    	{'\u00f0', '\u0057'},
    	{'\u00f1', '\u004e'},
    	{'\u00f2', '\u004f'},
    	{'\u00f3', '\u004f'},
    	{'\u00f4', '\u004f'},
    	{'\u00f5', '\u004f'},
    	{'\u00f6', '\u004f'},
    	{'\u00f7', '\u0054'},
    	{'\u00f8', '\u004f'},
    	{'\u00f9', '\u0055'},
    	{'\u00fa', '\u0055'},
    	{'\u00fb', '\u0055'},
    	{'\u00fc', '\u0055'},
    	{'\u00fd', '\u0059'},
    	{'\u00fe', '\u0059'},
    	{'\u00ff', '\u0059'},

    	/*
        {(byte)0xa0, 0x20},     // NBSP -> SP
        {(byte)0xa6, 0x53},     // S caron -> S
        {(byte)0xa8, 0x53},     // s caron -> s
        {(byte)0xb4, 0x5a},     // Z caron -> Z
        {(byte)0xb8, 0x5a},     // z caron -> z
        {(byte)0xbc, 0x01},     // OE ligature -> OE
        {(byte)0xbd, 0x01},     // oe ligature -> OE
        {(byte)0xbe, 0x59},     // Y diaeresis -> Y
        {(byte)0xc0, 0x41},     // A grave -> A
        {(byte)0xc1, 0x41},     // A acute -> A
        {(byte)0xc2, 0x41},     // A circumflex -> A
        {(byte)0xc3, 0x41},     // A tilde -> A
        {(byte)0xc4, 0x41},     // A diaeresis -> A
        {(byte)0xc5, 0x41},     // A ring -> A
        {(byte)0xc6, 0x03},     // AE ligature -> AE
        {(byte)0xc7, 0x43},     // C cedilla -> C
        {(byte)0xc8, 0x45},     // E grave -> E
        {(byte)0xc9, 0x45},     // E acute -> E
        {(byte)0xca, 0x45},     // E circumflex -> E
        {(byte)0xcb, 0x45},     // E diaeresis -> E
        {(byte)0xcc, 0x49},     // I grave -> I
        {(byte)0xcd, 0x49},     // I acute -> I
        {(byte)0xce, 0x49},     // I circumflex -> I
        {(byte)0xcf, 0x49},     // I diaeresis -> I
        {(byte)0xd0, 0x57},     // W circumflex -> W
        {(byte)0xd1, 0x4e},     // N tilde -> N
        {(byte)0xd2, 0x4f},     // O grave -> O
        {(byte)0xd3, 0x4f},     // O acute -> O
        {(byte)0xd4, 0x4f},     // O circumflex -> O
        {(byte)0xd5, 0x4f},     // O tilde -> O
        {(byte)0xd6, 0x4f},     // O diaeresis -> O
        {(byte)0xd7, 0x54},     // T dot -> T
        {(byte)0xd8, 0x4f},     // O stroke -> O
        {(byte)0xd9, 0x55},     // U grave -> U
        {(byte)0xda, 0x55},     // U acute -> U
        {(byte)0xdb, 0x55},     // U circumflex -> U
        {(byte)0xdc, 0x55},     // U diaeresis -> U
        {(byte)0xdd, 0x59},     // Y acute -> Y
        {(byte)0xde, 0x59},     // Y circumflex -> Y
        {(byte)0xdf, 0x53},     // S sharp -> S
        {(byte)0xe0, 0x41},     // a grave -> A
        {(byte)0xe1, 0x41},     // a acute -> A
        {(byte)0xe2, 0x41},     // a circumflex -> A
        {(byte)0xe3, 0x41},     // a tilde -> A
        {(byte)0xe4, 0x41},     // a diaeresis -> A
        {(byte)0xe5, 0x41},     // a ring -> A
        {(byte)0xe6, 0x03},     // ae ligature -> AE
        {(byte)0xe7, 0x43},     // c cedilla -> C
        {(byte)0xe8, 0x45},     // e grave -> E
        {(byte)0xe9, 0x45},     // e acute -> E
        {(byte)0xea, 0x45},     // e circumflex -> E
        {(byte)0xeb, 0x45},     // e diaeresis -> E
        {(byte)0xec, 0x49},     // i grave -> I
        {(byte)0xed, 0x49},     // i acute -> I
        {(byte)0xee, 0x49},     // i circumflex -> I
        {(byte)0xef, 0x49},     // i diaeresis -> I
        {(byte)0xf0, 0x57},     // w circumflex -> W
        {(byte)0xf1, 0x4e},     // n tilde -> N
        {(byte)0xf2, 0x4f},     // o grave -> O
        {(byte)0xf3, 0x4f},     // o acute -> O
        {(byte)0xf4, 0x4f},     // o circumflex -> O
        {(byte)0xf5, 0x4f},     // o tilde -> O
        {(byte)0xf6, 0x4f},     // o diaeresis -> O
        {(byte)0xf7, 0x54},     // t dot -> T
        {(byte)0xf8, 0x4f},     // o stroke -> O
        {(byte)0xf9, 0x55},     // u grave -> U
        {(byte)0xfa, 0x55},     // u acute -> U
        {(byte)0xfb, 0x55},     // u circumflex -> U
        {(byte)0xfc, 0x55},     // u diaeresis -> U
        {(byte)0xfd, 0x59},     // y acute -> Y
        {(byte)0xfe, 0x59},     // y circumflex -> Y
        {(byte)0xff, 0x59}      // y diaeresis -> Y
        */
    };

    private final static char[] regular, regularCap;
    static {
        // Initialize regular, regularCap
        // Uninitialized values are left as is
        regular = new char[0x200];
        for (int i=0; i<regularInit.length; i++) {
            regular[regularInit[i][0]] = regularInit[i][1];
        }
        regularCap = new char[0x200];
        for (int i=0; i<regularCapInit.length; i++) {
            regularCap[regularCapInit[i][0]] = regularCapInit[i][1];
        }
    }

    public static String convert(String in) {
    	StringBuilder sb = new StringBuilder(in.length());
        for (int i=0; i < in.length(); i++) {
            char b = in.charAt(i);
            if (b < 0x20) {
            	sb.append(b+0x20);
            } else if (b < 0x80) {
            	sb.append(b);
            } else if (b < 0x200) {
                char c = regular[b];
                if (c == 0) {
                    // res[j++] = b;
                    continue;
                } else if (c < 5) {
                    // Handle ligature
                    c--;
                	sb.append(ligature[c][1]);
                	sb.append(ligature[c][2]);
                } else {
                	sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    public static String convertAndCap(String in) {

    	StringBuilder sb = new StringBuilder(in.length());
        for (int i=0; i < in.length(); i++) {
            char b = in.charAt(i);
            if (b < 0x20) {
            	sb.append(b+0x20);
            } else if (b < 0x80) {
                if (b > 0x60 && b < 0x7b) {
                    b -= 0x20;
                }
            	sb.append(b);
            } else if (b < 0x200) {
                char c = regularCap[b];
                if (c == 0) {
                    continue;
                } else if (c < 5) {
                    // Handle ligature
                    c--;
                	sb.append(ligature[c][1]);
                	sb.append(ligature[c][2]);
                } else {
                	sb.append(c);
                }
            }
        }
        return sb.toString();
    }
}
