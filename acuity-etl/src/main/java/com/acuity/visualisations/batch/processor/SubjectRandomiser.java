/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.batch.processor;

import java.util.Random;

/**
 * Randomises a subject ID to meet
 *
 * @author krtv091
 */
public class SubjectRandomiser {

    private Random random = new Random();

    /**
     * Performs the main operation of randomising the subject ID
     *
     * @param subjectID The subject ID to randomise
     * @return A random subject ID, with letters replaced with a random letter
     *         and a series of numbers replaced with a random number string.
     *         The same letter and number will always produce the same letter or number
     */
    public String randomiseSubjectID(String subjectID) {

        // Loop through each character, generating a random letter if a letter is found
        // and a random number for a series of numbers
        StringBuilder randomSubjectID = new StringBuilder();
        StringBuilder numbers = new StringBuilder();

        for (char c : subjectID.toCharArray()) {
            if (Character.isLetter(c)) {

                // Randomise the number string
                if (numbers.length() != 0) {
                    randomSubjectID.append(randomiseNumberString(numbers.toString()));
                    numbers.delete(0, numbers.length());
                }

                // Randomise the character
                randomSubjectID.append(randomiseChar(c));

            } else {
                if (Character.isDigit(c)) {
                    // Create a number string, which will be randomised later
                    numbers.append(c);
                }
            }
        }

        if (numbers.length() != 0) {
            randomSubjectID.append(randomiseNumberString(numbers.toString()));
        }

        return randomSubjectID.toString();
    }

    /**
     * Randomises a string of numbers. The number string is supplied as the seed to
     * the random number. The seed will always produce the same (pseudo-) random number.
     *
     * @param numbers A string of the numbers to randomise
     * @return An random integer with a range of up to 999999999. A high range is required
     *         so that 99.99% of subject IDs up to 1 million are randomised to a unique
     *         number.
     */
    private int randomiseNumberString(String numbers) {
        this.random.setSeed(Integer.parseInt(numbers));
        return this.random.nextInt(999999999);
    }

    /**
     * Randomises a single character. The character is supplied as
     * the seed to the random number. The seed will always produce
     * the same (pseudo-) random number.
     *
     * @param c The character
     * @return A random, capitalised character
     */
    private char randomiseChar(char c) {
        // A large primary number is required to increase the range of pseudo-random numbers
        this.random.setSeed(c * 1073676286);
        return (char) (random.nextInt((int) 'Z' - (int) 'A') + (int) 'A');
    }
}
