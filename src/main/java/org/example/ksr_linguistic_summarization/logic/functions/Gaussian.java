package org.example.ksr_linguistic_summarization.logic.functions;

public class Gaussian implements MembershipFunction{
    private double m, sigma;

    public Gaussian(double m, double sigma) {
        this.m = m;
        this.sigma = sigma;
    }

    public double getMembership(double x) {
        double mem = Math.exp(-Math.pow(x - m, 2) / (2 * sigma * sigma));
        return (mem > 0.05) ? mem : 0;
    }
}
