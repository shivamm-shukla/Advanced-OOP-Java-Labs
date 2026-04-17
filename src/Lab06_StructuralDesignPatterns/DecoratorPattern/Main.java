package Lab06_StructuralDesignPatterns.DecoratorPattern;


//Component Interface
interface MessageProcessor {
    String process(String message);
}


//  Concrete Component
class PlainSender implements MessageProcessor {
    @Override
    public String process(String message) {
        System.out.println("[SENT] " + message);
        return message;
    }
}


// Base Decorator
abstract class MessageDecorator implements MessageProcessor {
    protected MessageProcessor wrapped;

    public MessageDecorator(MessageProcessor wrapped) {
        this.wrapped = wrapped;
    }
}


// Concrete Decorators

class EncryptionDecorator extends MessageDecorator {

    public EncryptionDecorator(MessageProcessor wrapped) {
        super(wrapped);
    }

    @Override
    public String process(String message) {
        // Caesar cipher — shift by 3
        StringBuilder encrypted = new StringBuilder();
        for (char c : message.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                encrypted.append((char) ((c - base + 3) % 26 + base));
            } else {
                encrypted.append(c);
            }
        }
        String result = encrypted.toString();
        System.out.println("[ENCRYPTED] " + result);
        return wrapped.process(result);
    }
}


class CompressionDecorator extends MessageDecorator {

    public CompressionDecorator(MessageProcessor wrapped) {
        super(wrapped);
    }

    @Override
    public String process(String message) {
        // Run-length encoding
        StringBuilder compressed = new StringBuilder();
        int i = 0;
        while (i < message.length()) {
            char c = message.charAt(i);
            int count = 1;
            while (i + count < message.length()
                    && message.charAt(i + count) == c) {
                count++;
            }
            compressed.append(c);
            if (count > 1) compressed.append(count);
            i += count;
        }
        String result = compressed.toString();
        System.out.println("[COMPRESSED] " + result);
        return wrapped.process(result);
    }
}


class TimestampDecorator extends MessageDecorator {

    public TimestampDecorator(MessageProcessor wrapped) {
        super(wrapped);
    }

    @Override
    public String process(String message) {
        String result = "[" + java.time.LocalTime.now().withNano(0) + "] " + message;
        System.out.println("[TIMESTAMPED] " + result);
        return wrapped.process(result);
    }
}


//  New Decorator added WITHOUT changing any existing class
class TranslationDecorator extends MessageDecorator {

    public TranslationDecorator(MessageProcessor wrapped) {
        super(wrapped);
    }

    @Override
    public String process(String message) {
        String result = message
                .replace("Hello", "Namaste")
                .replace("World", "Duniya");
        System.out.println("[TRANSLATED] " + result);
        return wrapped.process(result);
    }
}


// Main
public class Main {
    public static void main(String[] args) {

        String msg = "Hello World! This is a secret message.";

        // --- Plain ---
        System.out.println("=== Plain ===");
        new PlainSender().process(msg);

        // --- Encrypted only ---
        System.out.println("\n=== Encrypted ===");
        new EncryptionDecorator(
                new PlainSender()
        ).process(msg);

        // --- Compressed only ---
        System.out.println("\n=== Compressed ===");
        new CompressionDecorator(
                new PlainSender()
        ).process(msg);

        // --- Encrypted + Compressed ---
        System.out.println("\n=== Encrypted + Compressed ===");
        new EncryptionDecorator(
                new CompressionDecorator(
                        new PlainSender()
                )
        ).process(msg);

        // --- Compressed + Encrypted (order swapped — different result!) ---
        System.out.println("\n=== Compressed + Encrypted (order swapped) ===");
        new CompressionDecorator(
                new EncryptionDecorator(
                        new PlainSender()
                )
        ).process(msg);

        // --- Encrypted + Compressed + Timestamped ---
        System.out.println("\n=== Encrypted + Compressed + Timestamped ===");
        new TimestampDecorator(
                new EncryptionDecorator(
                        new CompressionDecorator(
                                new PlainSender()
                        )
                )
        ).process(msg);

        // --- Translated + Encrypted + Compressed (TranslationDecorator added without changing anything) ---
        System.out.println("\n=== Translated + Encrypted + Compressed ===");
        new TranslationDecorator(
                new EncryptionDecorator(
                        new CompressionDecorator(
                                new PlainSender()
                        )
                )
        ).process(msg);
    }
}