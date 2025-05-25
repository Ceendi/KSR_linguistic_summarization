package org.example.ksr_linguistic_summarization.logic.functions;

public class Triangular implements MembershipFunction {
    private double a, b, c;

    public Triangular(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public double getMembership(double x) {
        if (x <= a || x >= c) return 0;
        else if (x == b) return 1;
        else if (x < b) return (x - a) / (b - a);
        else return (c - x) / (c - b);
    }
}
