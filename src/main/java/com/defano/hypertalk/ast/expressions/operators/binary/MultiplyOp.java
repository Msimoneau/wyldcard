package com.defano.hypertalk.ast.expressions.operators.binary;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.operators.BinaryOperatorExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class MultiplyOp extends BinaryOperatorExp {

    public MultiplyOp(ParserRuleContext ctx, Expression lhs, Expression rhs) {
        super(ctx, lhs, rhs);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        return lhs(context).multiply(rhs(context));
    }
}
