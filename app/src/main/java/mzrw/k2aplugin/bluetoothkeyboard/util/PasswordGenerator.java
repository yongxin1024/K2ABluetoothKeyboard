package mzrw.k2aplugin.bluetoothkeyboard.util;

import java.util.Random;

public class PasswordGenerator {

    // 单词池
    private static final String[] WORD_POOL = {
            "Adventure", "Bright", "Courage", "Dream", "Energy", "Freedom", "Harmony",
            "Inspire", "Journey", "Knowledge", "Liberty", "Momentum", "Nature", "Optimism",
            "Passion", "Quality", "Resolve", "Spirit", "Treasure", "Unity", "Victory", "Wisdom",
            "Angelfish", "Barnacle", "Clam", "Cod", "Coral", "Crab", "Dolphin", "Eel",
            "Flounder", "Grouper", "Guppy", "Haddock", "Herring", "Jellyfish", "Krill",
            "Lobster", "Manatee", "Manta", "Marlin", "Minnow", "Mullet", "Octopus",
            "Oyster", "Perch", "Piranha", "Prawn", "Ray", "Salmon", "Scallop", "Seal",
            "Shark", "Shrimp", "Snapper", "Squid", "Stingray", "Swordfish", "Trout",
            "Tuna", "Urchin", "Whale", "Wrasse", "Antelope", "Badger", "Beaver", "Bison",
            "Buffalo", "Camel", "Cheetah", "Cougar",
            "Coyote", "Deer", "Donkey", "Elephant", "Ferret", "Fox", "Gazelle", "Giraffe",
            "Hedgehog", "Hippopotamus", "Horse", "Jaguar", "Kangaroo", "Koala", "Leopard",
            "Lion", "Lynx", "Mongoose", "Monkey", "Moose", "Ocelot", "Opossum", "Otter",
            "Panther", "Puma", "Rabbit", "Raccoon", "Rhino", "Sheep", "Sloth", "Squirrel",
            "Tiger", "Vole", "Wolf", "Yak", "Zebra"
    };

    // 特殊字符池
    private static final char[] SPECIAL_CHARACTERS = {'@', '#', '$', '%', '&', '*', '!', '?', '-', '_'};

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(generatePassword());
        }
    }

    public static String generatePassword() {
        Random random = new Random();

        // 随机选一个或两个单词
        String firstWord = getRandomWord(random);
        String secondWord = getRandomWord(random);


        // 随机选择一个特殊字符和一个数字
        char specialChar = SPECIAL_CHARACTERS[random.nextInt(SPECIAL_CHARACTERS.length)];
        int number = random.nextInt(10); // 0-9的数字


        return String.format("%s%s%s%d", firstWord, secondWord, specialChar, number);
    }

    // 随机获取一个单词，并首字母大写
    private static String getRandomWord(Random random) {
        String word = WORD_POOL[random.nextInt(WORD_POOL.length)];
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}
