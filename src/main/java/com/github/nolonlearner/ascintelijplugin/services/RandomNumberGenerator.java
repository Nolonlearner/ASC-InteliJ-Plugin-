package com.github.nolonlearner.ascintelijplugin.services;

import java.util.Random;

public class RandomNumberGenerator {
    private Random random;

    public RandomNumberGenerator() {
        random = new Random();
    }

    public int generateRandomNumber(int bound) {
        return random.nextInt(bound);
    }
}
