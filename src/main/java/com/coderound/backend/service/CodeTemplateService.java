package com.coderound.backend.service;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class CodeTemplateService {

    public String getStarterCode(String language) {
        if (language == null) {
            return getJavaStarterCode();
        }

        return switch (language.trim().toUpperCase(Locale.ROOT)) {
            case "PYTHON" -> getPythonStarterCode();
            case "CPP", "C++" -> getCppStarterCode();
            case "C" -> getCStarterCode();
            case "JAVASCRIPT", "JS" -> getJavaScriptStarterCode();
            default -> getJavaStarterCode();
        };
    }

    private String getJavaStarterCode() {
        return """
                import java.util.*;

                public class Main {
                    public static void main(String[] args) {
                        Scanner sc = new Scanner(System.in);
                        // Write your solution here
                    }
                }
                """;
    }

    private String getPythonStarterCode() {
        return """
                import sys

                def main():
                    input_data = sys.stdin.read().split()
                    if not input_data:
                        return
                    # Write your solution here

                if __name__ == '__main__':
                    main()
                """;
    }

    private String getCppStarterCode() {
        return """
                #include <iostream>
                #include <vector>
                using namespace std;

                int main() {
                    // Write your solution here
                    return 0;
                }
                """;
    }

    private String getCStarterCode() {
        return """
                #include <stdio.h>

                int main() {
                    // Write your solution here
                    return 0;
                }
                """;
    }

    private String getJavaScriptStarterCode() {
        return """
                const fs = require('fs');

                function main() {
                    const input = fs.readFileSync(0, 'utf-8').trim();
                    // Write your solution here
                }

                main();
                """;
    }
}
