/*
 * StatIf
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * StatIf.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of an if-then-else statement
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.constructs.ThenElseBlock;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

public class StatIf extends Statement {

    public final Expression condition;
    public final ThenElseBlock then;
    
    public StatIf (Expression condition, ThenElseBlock then) {
        this.condition = condition;
        this.then = then;
    }
    
    public void execute () throws HtException {
        if (condition.evaluate().booleanValue())
            then.thenBranch.execute();
        else
            then.elseBranch.execute();
    }
}
