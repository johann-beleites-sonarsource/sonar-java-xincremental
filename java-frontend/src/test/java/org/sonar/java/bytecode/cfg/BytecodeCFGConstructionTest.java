/*
 * SonarQube Java
 * Copyright (C) 2012-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.java.bytecode.cfg;

import com.google.common.collect.ImmutableList;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.Printer;

import org.sonar.java.bytecode.cfg.BytecodeCFGBuilder.Instruction.FieldOrMethod;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.objectweb.asm.Opcodes.*;

@RunWith(Parameterized.class)
public class BytecodeCFGConstructionTest {

  public static final String JAVA_LANG_OBJECT = "java.lang.Object";

  @Parameters(name = "{0}")
  public static Collection<Object[]> data() {
    ImmutableList.Builder<Object[]> testData = ImmutableList.builder();

    // Instructions without operand
    testData.addAll(
      Instructions.NO_OPERAND_INSN.stream()
        .map(opcode -> new Object[] {new TestInput(opcode), new BytecodeCFGBuilder.Instruction(opcode)})
        .collect(Collectors.toList()));

    // Instructions with int operand
    testData.add(new Object[] {intOp(BIPUSH, 1), inst(BIPUSH)});
    testData.add(new Object[] {intOp(SIPUSH, 1), inst(SIPUSH)});
    testData.add(new Object[] {intOp(NEWARRAY, 1), inst(NEWARRAY)});

    // LOAD STORE
    testData.add(new Object[] {intOp(ILOAD, 42), inst(ILOAD, 42)});
    testData.add(new Object[] {intOp(LLOAD, 42), inst(LLOAD, 42)});
    testData.add(new Object[] {intOp(FLOAD, 42), inst(FLOAD, 42)});
    testData.add(new Object[] {intOp(DLOAD, 42), inst(DLOAD, 42)});
    testData.add(new Object[] {intOp(ALOAD, 42), inst(ALOAD, 42)});
    testData.add(new Object[] {intOp(ISTORE, 42),inst(ISTORE, 42)});
    testData.add(new Object[] {intOp(LSTORE, 42),inst(LSTORE, 42)});
    testData.add(new Object[] {intOp(FSTORE, 42),inst(FSTORE, 42)});
    testData.add(new Object[] {intOp(DSTORE, 42),inst(DSTORE, 42)});
    testData.add(new Object[] {intOp(ASTORE, 42),inst(ASTORE, 42)});
    testData.add(new Object[] {intOp(RET, 42), inst(RET, 42)});

    // Instructions with type argument
    testData.add(new Object[] {new TestInput(NEW, JAVA_LANG_OBJECT), inst(NEW, JAVA_LANG_OBJECT)});
    testData.add(new Object[] {new TestInput(ANEWARRAY, JAVA_LANG_OBJECT), inst(ANEWARRAY, JAVA_LANG_OBJECT)});
    testData.add(new Object[] {new TestInput(CHECKCAST, JAVA_LANG_OBJECT), inst(CHECKCAST, JAVA_LANG_OBJECT)});
    testData.add(new Object[] {new TestInput(INSTANCEOF, JAVA_LANG_OBJECT), inst(INSTANCEOF, JAVA_LANG_OBJECT)});

    // Instructions with field argument
    testData.add(new Object[] {new TestInput(GETSTATIC, JAVA_LANG_OBJECT, "field", ""), null});
    testData.add(new Object[] {new TestInput(PUTSTATIC, JAVA_LANG_OBJECT, "field", ""), null});
    testData.add(new Object[] {new TestInput(GETFIELD, JAVA_LANG_OBJECT, "field", ""), null});
    testData.add(new Object[] {new TestInput(PUTFIELD, JAVA_LANG_OBJECT, "field", ""), null});

    // Instructions with method argument
    testData.add(new Object[] {new TestInput(INVOKESPECIAL, JAVA_LANG_OBJECT, "hashCode", "()I", false), inst(INVOKESPECIAL, JAVA_LANG_OBJECT, "hashCode", "()I", false)});
    testData.add(new Object[] {new TestInput(INVOKESTATIC, JAVA_LANG_OBJECT, "hashCode", "()I", false), inst(INVOKESTATIC, JAVA_LANG_OBJECT, "hashCode", "()I", false)});
    testData.add(new Object[] {new TestInput(INVOKEVIRTUAL, JAVA_LANG_OBJECT, "hashCode", "()I", false), inst(INVOKEVIRTUAL, JAVA_LANG_OBJECT, "hashCode", "()I", false)});
    testData.add(new Object[] {new TestInput(INVOKEINTERFACE, JAVA_LANG_OBJECT, "hashCode", "()I", false), inst(INVOKEINTERFACE, JAVA_LANG_OBJECT, "hashCode", "()I", false)});

    // Jump instructions
    testData.add(new Object[] {new TestInput(IFEQ), null});
    testData.add(new Object[] {new TestInput(IFNE), null});
    testData.add(new Object[] {new TestInput(IFLT), null});
    testData.add(new Object[] {new TestInput(IFGE), null});
    testData.add(new Object[] {new TestInput(IFGT), null});
    testData.add(new Object[] {new TestInput(IFLE), null});
    testData.add(new Object[] {new TestInput(IF_ICMPEQ), null});
    testData.add(new Object[] {new TestInput(IF_ICMPNE), null});
    testData.add(new Object[] {new TestInput(IF_ICMPLT), null});
    testData.add(new Object[] {new TestInput(IF_ICMPGE), null});
    testData.add(new Object[] {new TestInput(IF_ICMPGT), null});
    testData.add(new Object[] {new TestInput(IF_ICMPLE), null});
    testData.add(new Object[] {new TestInput(IF_ACMPEQ), null});
    testData.add(new Object[] {new TestInput(IF_ACMPNE), null});
    testData.add(new Object[] {new TestInput(GOTO), null});
    testData.add(new Object[] {new TestInput(JSR), null});
    testData.add(new Object[] {new TestInput(IFNULL), null});
    testData.add(new Object[] {new TestInput(IFNONNULL), null});

    // The rest
    testData.add(new Object[] {new TestInput(LDC), null});
    testData.add(new Object[] {new TestInput(IINC), null});
    testData.add(new Object[] {new TestInput(INVOKEDYNAMIC), null});
    testData.add(new Object[] {new TestInput(TABLESWITCH), null});
    testData.add(new Object[] {new TestInput(LOOKUPSWITCH), null});
    testData.add(new Object[] {new TestInput(MULTIANEWARRAY), null});

    return testData.build();
  }

  private static TestInput intOp(int opcode, int operand) {
    return new TestInput(opcode, operand);
  }

  private static BytecodeCFGBuilder.Instruction inst(int opcode) {
    return new BytecodeCFGBuilder.Instruction(opcode);
  }

  private static BytecodeCFGBuilder.Instruction inst(int opcode, int operand) {
    return new BytecodeCFGBuilder.Instruction(opcode, operand);
  }

  private static BytecodeCFGBuilder.Instruction inst(int opcode, String type) {
    return new BytecodeCFGBuilder.Instruction(opcode, type);
  }

  private static BytecodeCFGBuilder.Instruction inst(int opcode, String owner, String name, String desc, boolean itf) {
    return new BytecodeCFGBuilder.Instruction(opcode, new FieldOrMethod(owner, name, desc, itf));
  }

  static class TestInput {
    int opcode;
    int operandOrVar;
    String type;
    FieldOrMethod fieldOrMethod;

    @Override
    public String toString() {
      return Printer.OPCODES[opcode];
    }

    TestInput(int opcode) {
      this.opcode = opcode;
    }

    TestInput(int opcode, int operandOrVar) {
      this.opcode = opcode;
      this.operandOrVar = operandOrVar;
    }

    TestInput(int opcode, String type) {
      this.opcode = opcode;
      this.type = type;
    }

    TestInput(int opcode, String owner, String name, String desc) {
      this(opcode, owner, name, desc, false);
    }

    TestInput(int opcode, String owner, String name, String desc, boolean itf) {
      this.opcode = opcode;
      this.fieldOrMethod = new FieldOrMethod(owner, name, desc, itf);
    }
  }

  private TestInput testInput;
  private BytecodeCFGBuilder.Instruction expected;

  @BeforeClass
  public static void verifyTestData() {
    List<Integer> opcodes = data().stream().map(data -> ((TestInput) data[0]).opcode).collect(Collectors.toList());
    assertThat(opcodes).containsAll(Instructions.ASM_OPCODES);
  }

  public BytecodeCFGConstructionTest(TestInput testInput, BytecodeCFGBuilder.Instruction expected) {
    this.testInput = testInput;
    this.expected = expected;
  }

  @Test
  public void test() throws Exception {
    if (isJumpInstruction(testInput.opcode)) {
      test_jumps();
      return;
    }
    BytecodeCFGBuilder.BytecodeCFG cfg = new Instructions().cfg(testInput.opcode, testInput.operandOrVar, testInput.type, testInput.fieldOrMethod);
    assertThat(cfg.blocks.size()).isEqualTo(2);
    if(expected == null) {
      expected = inst(testInput.opcode);
    }
    BytecodeCFGBuilder.Instruction actual = cfg.blocks.get(1).instructions.get(0);
    assertThat(isEquivalentInstruction(actual, expected)).isTrue();
  }

  private static boolean isJumpInstruction(int opcode) {
    return Opcodes.IFEQ <= opcode && opcode <= LOOKUPSWITCH && opcode != RET || opcode==IFNULL || opcode==IFNONNULL;
  }

  private static boolean isEquivalentInstruction(BytecodeCFGBuilder.Instruction i1, BytecodeCFGBuilder.Instruction i2) {
    return i1.opcode == i2.opcode
      && Objects.equals(i1.operand, i2.operand)
      && Objects.equals(i1.className, i2.className)
      && Objects.equals(i1.fieldOrMethod, i2.fieldOrMethod);
  }

  private void test_jumps() {
    BytecodeCFGBuilder.BytecodeCFG cfg = new Instructions().cfg(testInput.opcode);
    assertThat(expected).isNull();
    if(testInput.opcode == TABLESWITCH) {
      assertThat(cfg.blocks.size()).isEqualTo(5);
    } else if(testInput.opcode == LOOKUPSWITCH) {
      assertThat(cfg.blocks.size()).isEqualTo(3);
    }else {
      // exit block, jump block, jump-to block, other block
      assertThat(cfg.blocks.size()).isEqualTo(4);
      assertThat(cfg.blocks.get(1).instructions).isEmpty();
      assertThat(cfg.blocks.get(1).terminator().opcode()).isEqualTo(testInput.opcode);
      assertThat(cfg.blocks.get(2).instructions.get(0).opcode()).isEqualTo(NOP);
      assertThat(cfg.blocks.get(3).instructions.get(0).opcode()).isEqualTo(ICONST_0);
    }
  }
}
