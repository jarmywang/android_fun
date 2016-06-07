package org.vkedco.opencv.haar;

import org.vkedco.opencv.haar.TwoDMatHaar.EDGE_TYPE;

public class EdgeStats {
	EDGE_TYPE mEdgeType;
	double mHAA; double mHRA;
	double mVAA; double mVRA;
	double mDAA; double mDRA;
	public EdgeStats(EDGE_TYPE et, double haa, double hra, double vaa, double vra, double daa, double dra) {
		mEdgeType = et;
		mHAA = haa;
		mHRA = hra;
		mVAA = vaa;
		mVRA = vra;
		mDAA = daa;
		mDRA = dra;
	}
}
