-- Seed DSA Questions for CodeRound Assessment Platform

-- Question 1: Two Sum (Easy)
INSERT INTO questions (title, description, difficulty, created_at)
VALUES (
    'Two Sum',
    'Given an array of integers nums and an integer target, return the 0-based indices of the two numbers such that they add up to target.

You may assume that each input would have exactly one solution, and you may not use the same element twice.

Input Format:
- Line 1: An integer N representing the size of the array
- Line 2: N space-separated integers representing nums
- Line 3: An integer target

Output Format:
- Print the two space-separated indices in ascending order.',
    'Easy',
    CURRENT_TIMESTAMP
);

-- Test cases for Question 1
INSERT INTO test_cases (question_id, input, expected_output, is_hidden, created_at) VALUES
((SELECT id FROM questions WHERE title = 'Two Sum'), '4
2 7 11 15
9', '0 1', false, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Two Sum'), '3
3 2 4
6', '1 2', false, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Two Sum'), '2
3 3
6', '0 1', true, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Two Sum'), '5
1 5 10 20 30
40', '2 4', true, CURRENT_TIMESTAMP);

-- Question 2: Valid Palindrome (Easy)
INSERT INTO questions (title, description, difficulty, created_at)
VALUES (
    'Valid Palindrome',
    'A phrase is a palindrome if, after converting all uppercase letters into lowercase letters and removing all non-alphanumeric characters, it reads the same forward and backward. Alphanumeric characters include letters and numbers.

Given a string s, return "true" if it is a palindrome, or "false" otherwise.

Input Format:
- A single line containing the string s.

Output Format:
- Print "true" or "false".',
    'Easy',
    CURRENT_TIMESTAMP
);

-- Test cases for Question 2
INSERT INTO test_cases (question_id, input, expected_output, is_hidden, created_at) VALUES
((SELECT id FROM questions WHERE title = 'Valid Palindrome'), 'A man, a plan, a canal: Panama', 'true', false, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Valid Palindrome'), 'race a car', 'false', false, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Valid Palindrome'), ' ', 'true', true, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Valid Palindrome'), '0P', 'false', true, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Valid Palindrome'), 'Was it a car or a cat I saw?', 'true', true, CURRENT_TIMESTAMP);

-- Question 3: Maximum Subarray (Medium)
INSERT INTO questions (title, description, difficulty, created_at)
VALUES (
    'Maximum Subarray',
    'Given an integer array nums, find the subarray with the largest sum, and return its sum.

A subarray is a contiguous non-empty sequence of elements within an array.

Input Format:
- Line 1: An integer N representing array length
- Line 2: N space-separated integers representing nums

Output Format:
- Print a single integer representing the maximum subarray sum.',
    'Medium',
    CURRENT_TIMESTAMP
);

-- Test cases for Question 3
INSERT INTO test_cases (question_id, input, expected_output, is_hidden, created_at) VALUES
((SELECT id FROM questions WHERE title = 'Maximum Subarray'), '9
-2 1 -3 4 -1 2 1 -5 4', '6', false, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Maximum Subarray'), '1
1', '1', false, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Maximum Subarray'), '5
5 4 -1 7 8', '23', true, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Maximum Subarray'), '4
-5 -2 -8 -1', '-1', true, CURRENT_TIMESTAMP);

-- Question 4: Valid Parentheses (Easy)
INSERT INTO questions (title, description, difficulty, created_at)
VALUES (
    'Valid Parentheses',
    'Given a string s containing just the characters ''('', '')'', ''{'', ''}'', ''['' and '']'', determine if the input string is valid.

An input string is valid if:
1. Open brackets must be closed by the same type of brackets.
2. Open brackets must be closed in the correct order.
3. Every close bracket has a corresponding open bracket of the same type.

Input Format:
- A single line containing the string s.

Output Format:
- Print "true" if the string is valid, otherwise print "false".',
    'Easy',
    CURRENT_TIMESTAMP
);

-- Test cases for Question 4
INSERT INTO test_cases (question_id, input, expected_output, is_hidden, created_at) VALUES
((SELECT id FROM questions WHERE title = 'Valid Parentheses'), '()', 'true', false, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Valid Parentheses'), '()[]{}', 'true', false, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Valid Parentheses'), '(]', 'false', true, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Valid Parentheses'), '([)]', 'false', true, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Valid Parentheses'), '{[]}', 'true', true, CURRENT_TIMESTAMP);

-- Question 5: Coin Change (Medium)
INSERT INTO questions (title, description, difficulty, created_at)
VALUES (
    'Coin Change',
    'You are given an integer array coins representing coins of different denominations and an integer amount representing a total amount of money.

Return the fewest number of coins that you need to make up that amount. If that amount of money cannot be made up by any combination of the coins, return -1.

You may assume that you have an infinite number of each kind of coin.

Input Format:
- Line 1: An integer N representing number of coin denominations
- Line 2: N space-separated integers representing coin values
- Line 3: An integer amount

Output Format:
- Print the minimum number of coins needed, or -1 if impossible.',
    'Medium',
    CURRENT_TIMESTAMP
);

-- Test cases for Question 5
INSERT INTO test_cases (question_id, input, expected_output, is_hidden, created_at) VALUES
((SELECT id FROM questions WHERE title = 'Coin Change'), '3
1 2 5
11', '3', false, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Coin Change'), '1
2
3', '-1', false, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Coin Change'), '1
1
0', '0', true, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Coin Change'), '4
1 5 6 9
11', '2', true, CURRENT_TIMESTAMP);

-- Question 6: Trapping Rain Water (Hard)
INSERT INTO questions (title, description, difficulty, created_at)
VALUES (
    'Trapping Rain Water',
    'Given n non-negative integers representing an elevation map where the width of each bar is 1, compute how much water it can trap after raining.

Input Format:
- Line 1: An integer N representing the number of bars
- Line 2: N space-separated integers representing the height of each bar

Output Format:
- Print a single integer representing total trapped rain water.',
    'Hard',
    CURRENT_TIMESTAMP
);

-- Test cases for Question 6
INSERT INTO test_cases (question_id, input, expected_output, is_hidden, created_at) VALUES
((SELECT id FROM questions WHERE title = 'Trapping Rain Water'), '12
0 1 0 2 1 0 1 3 2 1 2 1', '6', false, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Trapping Rain Water'), '6
4 2 0 3 2 5', '9', false, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Trapping Rain Water'), '3
2 0 2', '2', true, CURRENT_TIMESTAMP),
((SELECT id FROM questions WHERE title = 'Trapping Rain Water'), '5
3 0 0 2 0', '2', true, CURRENT_TIMESTAMP);
