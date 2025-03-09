# **Textual Calculator**

## **Introduction**
The **Textual Calculator** is a mathematical expression evaluator that processes expressions using **stacks** to maintain operator precedence. It follows a structured approach similar to the **Shunting Yard Algorithm**, ensuring correct computation of mathematical expressions with operators and parentheses.

---

## **1️⃣ How the Stack Works**
The **ExpressionCalculatorService** maintains **two stacks**:
- **`values` stack** → Stores numbers (operands).
- **`operators` stack** → Stores mathematical operators (`+`, `-`, `*`, `/`, etc.).

As the **expression is processed**, these stacks help **enforce correct operator precedence**.

---

## **2️⃣ Example: Evaluating `3 + 5 * 2`**
### **Step-by-Step Execution Using Stacks:**
| Token | Values Stack | Operators Stack | Explanation |
|--------|--------------|----------------|-------------|
| `3` | `[3]` | `[]` | Push **number** onto `values` stack. |
| `+` | `[3]` | `['+']` | Push **operator** onto `operators` stack. |
| `5` | `[3, 5]` | `['+']` | Push **number** onto `values` stack. |
| `*` | `[3, 5]` | `['+', '*']` | `*` has **higher precedence** than `+`, so push onto `operators`. |
| `2` | `[3, 5, 2]` | `['+', '*']` | Push **number** onto `values` stack. |

### **Now, Evaluate the Expression**
- Process `*` first (`5 * 2 = 10`) → Stack becomes:
    - **Values:** `[3, 10]`
    - **Operators:** `['+']`
- Process `+` (`3 + 10 = 13`) → Stack becomes:
    - **Values:** `[13]`
    - **Operators:** `[]`

✅ **Final result: `13`**

---

## **3️⃣ How Operator Precedence is Handled**
| Operator | Precedence | Associativity |
|----------|------------|---------------|
| `+`, `-` | 1 (Low) | Left |
| `*`, `/`, `%` | 2 (Higher) | Left |
| `^` (Exponentiation) | 3 (Highest) | Right |

### **Precedence Rules in the Stack**
1. **If the new operator has higher precedence** than the top of the stack, push it.
2. **If the new operator has lower/equal precedence**, pop the top operator and apply it before pushing the new operator.
3. **Parentheses**:
    - `(` → Always push onto `operators` stack.
    - `)` → Pop and evaluate everything until `(` is found.

---

## **4️⃣ Example: Evaluating `(3 + 5) * 2`**
| Token | Values Stack | Operators Stack | Explanation |
|--------|--------------|----------------|-------------|
| `(` | `[]` | `['(']` | Push opening parenthesis. |
| `3` | `[3]` | `['(']` | Push number. |
| `+` | `[3]` | `['(', '+']` | Push `+`. |
| `5` | `[3, 5]` | `['(', '+']` | Push number. |
| `)` | `[8]` | `[]` | Evaluate `3 + 5`. |
| `*` | `[8]` | `['*']` | Push `*`. |
| `2` | `[8, 2]` | `['*']` | Push number. |

### **Now, Evaluate the Expression**
- Process `*` (`8 * 2 = 16`).
- **Final result: `16`**.

---

## **5️⃣ Code Reference: Handling Operators**
### **In `handleOperator()`**
```java
private void handleOperator(String operator) throws InvalidInputException {
    IOperator currentOperator = OperatorFactory.getOperator(operator);
    
    if (currentOperator instanceof OpenParenthesisOperator) {
        operators.push(currentOperator);  // Always push '('
    }
    else if (currentOperator instanceof CloseParenthesisOperator) {
        // Process everything inside parentheses
        while (!operators.isEmpty() && !(operators.peek() instanceof OpenParenthesisOperator)) {
            processOperator();
        }
        if (!operators.isEmpty() && operators.peek() instanceof OpenParenthesisOperator) {
            operators.pop(); // Remove '('
        } else {
            throw new InvalidInputException("Mismatched parentheses");
        }
    }
    else {
        // Handle standard operators
        while (!operators.isEmpty() && OperatorFactory.hasHigherPrecedence(operators.peek(), currentOperator)) {
            processOperator();
        }
        operators.push(currentOperator);
    }
}
```
✔ **Ensures correct order of operations** using stacks.

---

## **6️⃣ Summary**
- **Uses two stacks** (`values` and `operators`) to evaluate expressions.
- **Handles operator precedence** (e.g., `*` before `+`).
- **Supports parentheses** to enforce correct evaluation order.

🚀 **Now you understand how stacks handle precedence in the Textual Calculator!** 🚀

