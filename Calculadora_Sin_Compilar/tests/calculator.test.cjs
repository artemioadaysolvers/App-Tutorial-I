const test = require('node:test');
const assert = require('node:assert/strict');
const Calculator = require('../app/src/main/assets/www/engine.js');

function sequence(keys) {
  const calculator = new Calculator();
  keys.forEach(key => calculator.press(key));
  return calculator;
}
function expectResult(keys, expected) {
  assert.equal(sequence(keys).view().result, expected);
}

test('sumar, restar, multiplicar y dividir', () => {
  expectResult(['1', '2', '+', '3', 'equals'], '15');
  expectResult(['4', '-', '9', 'equals'], '-5');
  expectResult(['6', '*', '7', 'equals'], '42');
  expectResult(['9', '/', '4', 'equals'], '2,25');
});
test('decimales sin residuos de coma flotante', () => {
  expectResult(['decimal', '1', '+', 'decimal', '2', 'equals'], '0,3');
  expectResult(['2', 'decimal', 'decimal', '5'], '2,5');
});
test('cambiar operador antes de introducir el segundo número', () => {
  expectResult(['8', '+', '*', '2', 'equals'], '16');
});
test('operaciones encadenadas de izquierda a derecha', () => {
  expectResult(['2', '+', '3', '*', '4', 'equals'], '20');
});
test('igual repetido reutiliza la última operación', () => {
  expectResult(['2', '+', '3', 'equals', 'equals'], '8');
  expectResult(['5', '+', 'equals'], '10');
});
test('permite seguir operando con un resultado o empezar otro', () => {
  expectResult(['2', '+', '3', 'equals', '*', '4', 'equals'], '20');
  expectResult(['2', '+', '3', 'equals', '9'], '9');
  expectResult(['2', '+', '3', 'equals', 'decimal', '5'], '0,5');
});
test('signo para primer número, segundo número y resultado', () => {
  expectResult(['5', 'sign'], '-5');
  expectResult(['5', '+', 'sign', '2', 'equals'], '3');
  expectResult(['5', '+', '2', 'equals', 'sign'], '-7');
  expectResult(['sign', 'decimal', '5'], '-0,5');
});
test('porcentaje simple equivale a dividir entre cien', () => {
  expectResult(['5', '0', 'percent'], '0,5');
  expectResult(['2', '0', '0', '*', '1', '0', 'percent', 'equals'], '20');
});
test('borrar dígitos, limpiar y evitar ceros iniciales', () => {
  expectResult(['1', '2', '3', 'backspace'], '12');
  expectResult(['sign', 'backspace'], '0');
  expectResult(['7', '+', '3', 'clear'], '0');
  expectResult(['0', '0', '8'], '8');
});
test('división entre cero muestra error y permite recuperarse', () => {
  const calculator = sequence(['9', '/', '0', 'equals']);
  assert.equal(calculator.view().error, true);
  assert.match(calculator.view().message, /cero/);
  assert.equal(calculator.press('7').result, '7');
  expectResult(['1', '/', '0', 'equals', 'decimal', '5'], '0,5');
  expectResult(['1', '/', '0', 'equals', 'backspace'], '0');
});
test('límite de doce dígitos y resultados grandes', () => {
  expectResult(Array.from('1234567890123'), '123456789012');
  const calculator = sequence(Array.from('999999999999').concat(['*'], Array.from('999999999999'), ['equals']));
  assert.equal(calculator.view().error, false);
  assert.ok(calculator.view().result.length <= 16);
});
test('división entre cero negativo y redondeo estable', () => {
  assert.equal(sequence(['4', '/', 'sign', 'equals']).view().error, true);
  expectResult(['1', '/', '3', 'equals'], '0,333333333333');
});
