package org.example.ksr_linguistic_summarization.logic.functions;

public class Trapezoidal implements MembershipFunction {
    private final double a;
    private final double b;
    private final double c;
    private final double d;

    public Trapezoidal(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public double getMembership(double x) {
        if (x < a || x > d) return 0;
        else if (x >= b && x <= c) return 1;
        else if (x < b) return (x - a) / (b - a);
        else return (d - x) / (d - c);
    }
}
