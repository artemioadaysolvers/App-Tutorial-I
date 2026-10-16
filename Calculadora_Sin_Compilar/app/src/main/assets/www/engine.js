/* Pure JavaScript calculation logic, shared by the app and its tests. */
(function (root) {
  "use strict";
  const symbols = { "+": "+", "-": "−", "*": "×", "/": "÷" };
  const readable = value => String(value).replace(".", ",");

  class Calculator {
    constructor() { this.clear(); }

    clear() {
      this.entry = "0";
      this.accumulator = null;
      this.operator = null;
      this.waiting = false;
      this.justEvaluated = false;
      this.lastOperator = null;
      this.lastOperand = null;
      this.expression = "";
      this.error = "";
      this.hint = "Un cálculo a la vez.";
    }

    normalize(value) {
      if (!Number.isFinite(value)) throw new Error("El resultado está fuera de rango.");
      // Avoid showing floating-point artifacts such as 0.1 + 0.2 = 0.30000000000000004.
      return Number(value.toPrecision(12));
    }

    calculate(left, right, operator) {
      if (operator === "/" && right === 0) throw new Error("No se puede dividir entre cero.");
      switch (operator) {
        case "+": return this.normalize(left + right);
        case "-": return this.normalize(left - right);
        case "*": return this.normalize(left * right);
        case "/": return this.normalize(left / right);
        default: return right;
      }
    }

    startInput(initial) {
      if (this.error) this.clear();
      if (this.waiting || this.justEvaluated) {
        if (this.justEvaluated) {
          this.expression = "";
          this.lastOperator = null;
          this.lastOperand = null;
        }
        this.entry = initial;
        this.waiting = false;
        this.justEvaluated = false;
        return true;
      }
      return false;
    }

    press(key) {
      this.hint = "Un cálculo a la vez.";
      if (key === "clear") { this.clear(); return this.view(); }
      try {
        if (/^[0-9]$/.test(key)) {
          if (!this.startInput(key)) {
            const digitCount = this.entry.replace(/[^0-9]/g, "").length;
            if (this.entry === "0") this.entry = key;
            else if (this.entry === "-0") this.entry = "-" + key;
            else if (digitCount < 12) this.entry += key;
            else this.hint = "Puedes introducir hasta 12 dígitos.";
          }
        } else if (key === "decimal") {
          this.startInput("0");
          if (!this.entry.includes(".") && !this.entry.includes("e")) this.entry += ".";
        } else if (!this.error && key in symbols) {
          if (this.operator && !this.waiting) {
            this.entry = String(this.calculate(this.accumulator, Number(this.entry), this.operator));
          }
          this.accumulator = Number(this.entry);
          this.operator = key;
          this.expression = readable(this.entry) + " " + symbols[key];
          this.waiting = true;
          this.justEvaluated = false;
          this.lastOperator = null;
          this.lastOperand = null;
        } else if (!this.error && key === "equals") {
          const operation = this.operator || (this.justEvaluated ? this.lastOperator : null);
          if (operation) {
            const left = this.operator ? this.accumulator : Number(this.entry);
            const right = this.operator ? Number(this.entry) : this.lastOperand;
            const result = this.calculate(left, right, operation);
            this.expression = readable(left) + " " + symbols[operation] + " " + readable(right) + " =";
            this.entry = String(result);
            this.lastOperator = operation;
            this.lastOperand = right;
            this.operator = null;
            this.accumulator = null;
            this.waiting = false;
            this.justEvaluated = true;
            this.hint = "Resultado";
          }
        } else if (!this.error && key === "sign") {
          if (this.waiting) { this.entry = "0"; this.waiting = false; }
          this.entry = this.entry.startsWith("-") ? this.entry.slice(1) : "-" + this.entry;
          if (!this.operator) this.expression = "";
          this.justEvaluated = false;
          this.lastOperator = null;
        } else if (!this.error && key === "percent") {
          const previous = this.entry;
          this.entry = String(this.normalize(Number(this.entry) / 100));
          if (!this.operator) this.expression = readable(previous) + " %";
          this.waiting = false;
          this.justEvaluated = false;
          this.lastOperator = null;
          this.hint = "Porcentaje = número ÷ 100";
        } else if (key === "backspace") {
          if (this.error || this.justEvaluated) this.clear();
          else if (!this.waiting) {
            this.entry = this.entry.includes("e") ? "0" : this.entry.slice(0, -1);
            if (!this.entry || this.entry === "-") this.entry = "0";
            if (!this.operator) this.expression = "";
          }
        }
      } catch (error) {
        this.error = error.message;
        this.operator = null;
        this.waiting = false;
        this.justEvaluated = false;
        this.lastOperator = null;
      }
      return this.view();
    }

    view() {
      let display = this.entry;
      const number = Number(display);
      if (!this.error && display.length > 16) display = number.toExponential(7);
      return {
        result: this.error ? "Error" : readable(display),
        expression: this.expression || "Lista para calcular",
        message: this.error || this.hint,
        error: Boolean(this.error),
        activeOperator: this.waiting ? this.operator : null
      };
    }
  }

  if (typeof module !== "undefined" && module.exports) module.exports = Calculator;
  else root.Calculator = Calculator;
})(typeof globalThis !== "undefined" ? globalThis : this);
