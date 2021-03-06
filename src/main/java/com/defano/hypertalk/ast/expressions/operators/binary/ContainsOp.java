package com.defano.hypertalk.ast.expressions.operators.binary;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.operators.BinaryOperatorExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class ContainsOp extends BinaryOperatorExp {

    public ContainsOp(ParserRuleContext ctx, Expression lhs, Expression rhs) {
        super(ctx, lhs, rhs);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        return new Value(lhs(context).contains(rhs(context)));
    }
}
