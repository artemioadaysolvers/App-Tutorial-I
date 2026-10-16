(function () {
  "use strict";
  const calculator = new Calculator();
  const result = document.getElementById("result");
  const expression = document.getElementById("expression");
  const message = document.getElementById("message");
  const display = document.querySelector(".display");
  const operators = Array.from(document.querySelectorAll(".operator"));

  function render(state) {
    result.textContent = state.result;
    expression.textContent = state.expression;
    message.textContent = state.message;
    result.classList.toggle("medium", state.result.length > 9 && state.result.length <= 13);
    result.classList.toggle("small", state.result.length > 13);
    display.classList.toggle("has-error", state.error);
    operators.forEach(button => {
      button.setAttribute("aria-pressed", String(button.dataset.key === state.activeOperator));
    });
  }

  document.getElementById("keypad").addEventListener("click", event => {
    const button = event.target.closest("button[data-key]");
    if (button) render(calculator.press(button.dataset.key));
  });

  document.addEventListener("keydown", event => {
    if (event.ctrlKey || event.metaKey || event.altKey) return;
    let key = event.key;
    if (/^[0-9+*/-]$/.test(key)) { /* Already matches a calculator key. */ }
    else if (key === "." || key === ",") key = "decimal";
    else if (key === "=" || key === "Enter") {
      // A focused button uses its native Enter/Space action for keyboard accessibility.
      if (key === "Enter" && document.activeElement.tagName === "BUTTON") return;
      key = "equals";
    }
    else if (key === "Escape" || key === "Delete") key = "clear";
    else if (key === "Backspace") key = "backspace";
    else if (key === "%") key = "percent";
    else return;
    event.preventDefault();
    render(calculator.press(key));
  });

  render(calculator.view());
})();
