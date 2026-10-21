package com.retrolab.casiofx2200

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.*

class CalculatorEngineTest {
    private fun engine(vararg keys: String, mode: String="DEG"): CalculatorEngine = CalculatorEngine().apply { setMode(mode); keys.forEach { press(it) } }
    private fun check(expected: Double, vararg keys: String, mode: String="DEG") {
        val e=engine(*keys,mode=mode); assertEquals(e.error,"",e.error); assertEquals(expected,e.value,max(1e-12,abs(expected)*1e-12))
    }
    @Test fun sequentialArithmetic(){check(20.0,"2","+","3","*","4","=")}
    @Test fun arithmetic(){check(9.0,"3","6","/","4","=");check(-3.0,"2","-","5","=");check(7.0,"3","+","4","=")}
    @Test fun replacementOperator(){check(6.0,"2","+","*","3","=")}
    @Test fun repeatedEquals(){check(11.0,"2","+","3","=","=","=")}
    @Test fun clearEntryKeepsOperation(){check(11.0,"8","+","9","C","3","=")}
    @Test fun allClearCancelsOperation(){check(3.0,"8","+","9","AC","3","=")}
    @Test fun functionsInPendingExpression(){check(5.0,"2","+","9","SQRT","=")}
    @Test fun degrees(){check(.5,"3","0","SIN");check(.5,"6","0","COS");check(1.0,"4","5","TAN")}
    @Test fun radians(){check(.5,"INV","EXP","/","6","=","SIN",mode="RAD")}
    @Test fun grads(){check(1.0,"1","0","0","SIN",mode="GRA")}
    @Test fun inverseTrig(){check(30.0,".","5","INV","SIN");check(60.0,".","5","INV","COS");check(45.0,"1","INV","TAN")}
    @Test fun hyperbolic(){check(sinh(1.0),"1","HYP","SIN");check(1.0,"1","HYP","SIN","INV","HYP","SIN")}
    @Test fun inverseHyperbolicDomains(){assertTrue(engine("0","INV","HYP","COS").error.isNotEmpty());assertTrue(engine("1","INV","HYP","TAN").error.isNotEmpty())}
    @Test fun logarithms(){check(3.0,"1","0","0","0","LOG");check(100.0,"2","INV","LOG");check(1.0,"1","INV","LN","LN")}
    @Test fun powersAndRoots(){check(1024.0,"2","POW","1","0","=");check(3.0,"2","7","INV","POW","3","=");check(-3.0,"2","7","SIGN","INV","POW","3","=")}
    @Test fun rootAndReciprocal(){check(3.0,"9","SQRT");check(.25,"4","RECIP")}
    @Test fun factorial(){check(1.0,"0","FACT");check(120.0,"5","FACT");assertTrue(engine("7","0","FACT").error.isNotEmpty())}
    @Test fun exponentEntry(){check(.002,"2","EXP","SIGN","3","=");check(2000.0,"2","EXP","3","=")}
    @Test fun exponentImplicitOne(){check(1000.0,"EXP","3","=")}
    @Test fun signAndDecimal(){check(-2.5,"2",".","5","SIGN","=")}
    @Test fun backspace(){check(12.0,"1","2","3","BACKSPACE","=")}
    @Test fun fractions(){check(.5,"1","FRAC","2","=");check(7.0/3,"2","FRAC","1","FRAC","3","=");check(2.5,"2","+","1","FRAC","2","=")}
    @Test fun negativeMixedFraction(){check(-7.0/3,"2","SIGN","FRAC","1","FRAC","3","=")}
    @Test fun fractionZeroDenominator(){assertTrue(engine("1","FRAC","0","=").error.isNotEmpty())}
    @Test fun sexagesimal(){check(12.5,"1","2","DMS","3","0","DMS","0","DMS");check(12.5,"1","2","DMS","3","0","=")}
    @Test fun sexagesimalDisplay(){assertEquals("12°30′0″",engine("1","2",".","5","INV","DMS").display())}
    @Test fun sexagesimalRoundTrip(){assertEquals("14°25′36″",engine("1","4","DMS","2","5","DMS","3","6","DMS","INV","DMS").display())}
    @Test fun sexagesimalInvalidMinutes(){assertTrue(engine("1","DMS","6","0","DMS","0","DMS").error.isNotEmpty())}
    @Test fun memory(){check(12.0,"8","MIN","AC","4","MPLUS","MR");check(5.0,"8","MIN","3","SIGN","MPLUS","MR")}
    @Test fun memoryAfterExpression(){check(12.0,"2","+","1","0","MPLUS","MR")}
    @Test fun clearMemory(){assertEquals(0.0,engine("8","MIN","MC").memory,0.0)}
    private fun data()=engine("2","MPLUS","4","MPLUS","6","MPLUS",mode="SD")
    @Test fun statisticalAggregates(){for((key,result) in listOf("FACT" to 3.0,"FRAC" to 56.0,"RECIP" to 12.0,"SQRT" to 4.0,"MIN" to 2.0,"MR" to sqrt(8.0/3))){val e=data();e.press(key);assertEquals(e.error,"",e.error);assertEquals(result,e.value,1e-12)}}
    @Test fun removeStatisticalData(){val e=data();e.press("INV");e.press("MPLUS");assertEquals(2,e.count);e.press("SQRT");assertEquals(3.0,e.value,0.0)}
    @Test fun statisticsClearAndError(){val e=data();e.press("AC");assertEquals(3,e.count);e.press("SAC");assertEquals(0,e.count);e.press("MIN");assertTrue(e.error.isNotEmpty())}
    @Test fun statisticMissingRemoval(){assertTrue(engine("1","INV","MPLUS",mode="SD").error.isNotEmpty())}
    @Test fun modes(){val e=CalculatorEngine();assertEquals("RAD",e.mode);listOf("DEG","GRA","SD","RAD").forEach{e.press("MODE");assertEquals(it,e.mode)}}
    @Test fun domainAndRecovery(){for(k in listOf(arrayOf("1","/","0","="),arrayOf("1","SIGN","SQRT"),arrayOf("0","LN"),arrayOf("2","INV","SIN"),arrayOf("9","0","TAN"))){val e=engine(*k);assertTrue(k.joinToString(),e.error.isNotEmpty());assertEquals("Error",e.display());e.press("AC");e.press("2");assertEquals("2",e.display())}}
    @Test fun errorLocksEntryUntilClear(){val e=engine("1","/","0","=");e.press("9");assertEquals("Error",e.display());e.press("C");assertEquals("0",e.display())}
    @Test fun eightDigitEntry(){val e=engine("1","2","3","4","5","6","7","8","9");assertEquals("12345678",e.display())}
    @Test fun eightDigitResult(){assertEquals("0.33333333",engine("1","/","3","=").display())}
    @Test fun scientificDisplay(){assertEquals("1 E+8",engine("1","EXP","8","=").display());assertEquals("1 E-8",engine("1","EXP","SIGN","8","=").display())}
    @Test fun largeTrigInputIsError(){assertTrue(engine("3","6","EXP","7","SIN").error.isNotEmpty())}
    @Test fun powerSwitch(){val e=engine("8","MIN","POWER");assertEquals("",e.display());e.press("4");assertEquals("",e.display());e.press("POWER");assertEquals("0",e.display());assertEquals(8.0,e.memory,0.0)}
    @Test fun unaryStartsFreshEntry(){check(4.0,"9","SQRT","4","=")}
    @Test fun underflowAndOverflow(){val e=engine("9","9","INV","LN");assertTrue(e.error.isEmpty());e.press("INV");e.press("LN");assertTrue(e.error.isNotEmpty())}
}
