var calculadora_basica = Titanium.UI.createWindow({
	title : 'Calculadora básica',
	backgroundColor : '#fff'
});

var array_numeros_basica = [[7, 8, 9], [4, 5, 6], [1, 2, 3], [0, 'CE', '=']];

var array_numeros_cientifica = [[7, 8, 9], [4, 5, 6], [1, 2, 3], [0, 'CE', '='], ['pi', 'e', '+/-'], ['ln2', 'ln10', 'log2e']];

var array_operaciones_basica = ['+', '-', '/', '*'];

var array_operaciones_cientifica = [['+', '-', '*'], ['/', '1/x', 'x^y'], ['x^2', 'x^3', 'x!'], ['sqrt', 'ln', 'exp'], ['sin', 'cos', 'tan'], ['round', 'abs', '%']];

var operando1 = 0;
var operando2 = 0;
var operacion = '';
var resultado = 0;

var vista_display = Titanium.UI.createTextField({
	font : {
		fontSize : 20,
		fontFamily : 'Helvetica Neue'
	},
	width : '90%',
	height : '10%',
	top : '5%',
	left : '5%',
	textAlign : 'right',
	backgroundColor : '#33cc66',
	value : 0,
	borderStyle : Titanium.UI.INPUT_BORDERSTYLE_ROUNDED
});

var vista_numeros_basica = Ti.UI.createView({
	width : '70%',
	height : '70%',
	top : '20%',
	left : '5%',
});

var vista_operaciones_basica = Ti.UI.createView({
	width : '15%',
	height : '70%',
	top : '20%',
	left : '80%'
});

var vista_display_cientifica = Titanium.UI.createTextField({
	font : {
		fontSize : 20,
		fontFamily : 'Helvetica Neue'
	},
	width : '90%',
	height : '10%',
	top : '5%',
	left : '5%',
	textAlign : 'right',
	backgroundColor : '#33ff99',
	value : 0,
	borderStyle : Titanium.UI.INPUT_BORDERSTYLE_ROUNDED
});

var vista_numeros_cientifica = Ti.UI.createView({
	width : '40%',
	height : '70%',
	top : '20%',
	left : '5%',
});

var vista_operaciones_cientifica = Ti.UI.createView({
	width : '60%',
	height : '70%',
	top : '20%',
	left : '42%'
});

(function() {
	for (var i = 0; i < array_numeros_basica.length; i++) {
		for (var l = 0; l < array_numeros_basica[i].length; l++) {
			var label_numero = Titanium.UI.createButton({
				textAlign : 'center',
				color : 'black',
				width : '30%',
				height : '20%',
				left : l * 33 + '%',
				top : i * 22 + '%',
				id : array_numeros_basica[i][l],
				title : array_numeros_basica[i][l],
				font : {
					fontSize : 20,
					fontWeight : 'bold',
					fontFamily : 'Helvetica Neue'
				}

			});
			label_numero.addEventListener('click', function(e) {
				if (vista_display.value == 0) {
					vista_display.value = e.source.id + '';
				} else {
					vista_display.value = vista_display.value + '' + e.source.id;
				}
				if (e.source.id == 'CE') {

					teclaCE();
				}

				if (operacion == '') {
					operando1 = vista_display.value;
				}

				if ((operacion == '+') || (operacion == '-') || (operacion == '*') || (operacion == '/') || (operacion == '^')) {
					operando2 = operando2 + '' + e.source.id;
				}

				if (e.source.title == '=') {
					vista_display.value = vista_display.value + '' + e.source.id;
					vista_display.value = calcular(operando1, operando2, operacion);
					operando2 = 0;
					operacion = '';
					operando1 = resultado;
				}

			});
			vista_numeros_basica.add(label_numero);
		}
	}
	for (var v = 0; v < array_operaciones_basica.length; v++) {
		var label_operacion = Titanium.UI.createButton({
			textAlign : 'center',
			color : 'blue',
			width : '90%',
			height : '20%',
			top : v * 22 + '%',
			id : array_operaciones_basica[v],
			title : array_operaciones_basica[v],
			font : {
				fontSize : 20,
				fontWeight : 'bold',
				fontFamily : 'Helvetica Neue'
			}
		});
		label_operacion.addEventListener('click', function(e) {
			if (e.source.id != '=') {
				vista_display.value = vista_display.value + '' + e.source.id;
				operacion = e.source.id;

				if (operando1 != '0' && operando2 != '0') {
					vista_display.value = calcular(operando1, operando2, operacion);
					operando2 = 0;
					operacion = '';
					operando1 = resultado;
				}

			}

		});
		vista_operaciones_basica.add(label_operacion);
	}
})();

function calcular(operando1, operando2, operacion) {

	if (operando2.length > 1) {

		operando2 = operando2.substring(1, operando2.length);
	}

	operando1 = parseFloat(operando1);
	operando2 = parseFloat(operando2);

	switch (operacion) {
		case '+':
			resultado = operando1 + operando2;
			break;
		case '-':
			resultado = operando1 - operando2;
			break;
		case '*':
			resultado = operando1 * operando2;
			break;
		case '/':
			resultado = operando1 / operando2;
			break;
		case 'x^y':
			resultado = Math.pow(operando1, operando2);
			break;
	}

	return resultado.toString();

}

function calcularPI(operando1, operando2, operacion) {

	operando1 = parseFloat(operando1);
	operando2 = parseFloat(operando2);

	switch (operacion) {
		case '+':
			resultado = operando1 + operando2;
			break;
		case '-':
			resultado = operando1 - operando2;
			break;
		case '*':
			resultado = operando1 * operando2;
			break;
		case '/':
			resultado = operando1 / operando2;
			break;
		case 'x^y':
			resultado = Math.pow(operando1, operando2);
			break;
	}

	return resultado.toString();

}

//Funcion tecla CE basica
function teclaCE() {
	operando1 = 0;
	operando2 = 0;
	resultado = 0;
	operacion = '';

	vista_display.value = 0 + '';
}

//Funcion factorial
function factorial(n) {
	if (n == 0 || n == 1)
		return 1;
	else
		return n * factorial(n - 1);
}

//Funcion tecla CE cientifica
function teclaCE2() {
	operando1 = 0;
	operando2 = 0;
	resultado = 0;
	operacion = '';

	vista_display_cientifica.value = 0 + '';
}

//Funcion tecla PI cientifica
function teclaPI() {
	if (operando1 != '0' && operando2 == '0' && operacion != '') {
		operando2 = Math.PI;
		vista_display_cientifica.value = operando1 + operacion + Math.PI;
	}

	if (operando1 == '0' && operando2 == '0') {
		operando1 = Math.PI;
		vista_display_cientifica.value = '' + Math.PI;
	}

}

//Funcion tecla e cientifica
function teclaE() {
	operando1 = 0;
	operando2 = 0;
	operacion = '';

	vista_display_cientifica.value = Math.E + '';
}

//Funcion tecla ln2 cientifica
function teclaLN2() {
	operando1 = 0;
	operando2 = 0;
	operacion = '';

	vista_display_cientifica.value = Math.LN2 + '';
}

//Funcion tecla ln10 cientifica
function teclaLN10() {
	operando1 = 0;
	operando2 = 0;
	operacion = '';

	vista_display_cientifica.value = Math.LN10 + '';
}

//Funcion tecla lOG2E cientifica
function teclaLOG2E() {
	operando1 = 0;
	operando2 = 0;
	operacion = '';

	vista_display_cientifica.value = Math.LOG2E + '';
}

calculadora_basica.add(vista_operaciones_basica);
calculadora_basica.add(vista_display);
calculadora_basica.add(vista_numeros_basica);

// grupos de pestañas
var tabGroup = Titanium.UI.createTabGroup();

var tab1 = Titanium.UI.createTab({
	title : 'Básica',
	window : calculadora_basica
});

var cientifica = Titanium.UI.createWindow({
	title : 'Calcularora Científica',
	backgroundColor : '#fff'
});

(function() {
	for (var i = 0; i < array_numeros_cientifica.length; i++) {
		for (var l = 0; l < array_numeros_cientifica[i].length; l++) {

			var label_numero;

			if (array_numeros_cientifica[i][l] == 'pi') {

				label_numero = Titanium.UI.createButton({
					image : 'images/pi.png',
					color : 'black',
					width : '30%',
					height : '15%',
					left : l * 30 + '%',
					top : i * 15 + '%',
					id : array_numeros_cientifica[i][l]

				});

			} else if (array_numeros_cientifica[i][l] == 'ln2') {

				label_numero = Titanium.UI.createButton({
					color : 'black',
					width : '30%',
					height : '15%',
					left : l * 30 + '%',
					top : i * 15 + '%',
					id : array_numeros_cientifica[i][l],
					title : array_numeros_cientifica[i][l],
					font : {
						fontSize : 15,
						fontWeight : 'bold',
						fontFamily : 'Helvetica Neue'
					}
				});

			} else if (array_numeros_cientifica[i][l] == 'ln10') {

				label_numero = Titanium.UI.createButton({
					color : 'black',
					width : '30%',
					height : '15%',
					left : l * 30 + '%',
					top : i * 15 + '%',
					id : array_numeros_cientifica[i][l],
					title : array_numeros_cientifica[i][l],
					font : {
						fontSize : 11,
						fontWeight : 'bold',
						fontFamily : 'Helvetica Neue'
					}
				});

			} else if (array_numeros_cientifica[i][l] == 'log2e') {

				label_numero = Titanium.UI.createButton({
					color : 'black',
					width : '30%',
					height : '15%',
					left : l * 30 + '%',
					top : i * 15 + '%',
					id : array_numeros_cientifica[i][l],
					title : array_numeros_cientifica[i][l],
					font : {
						fontSize : 9,
						fontWeight : 'bold',
						fontFamily : 'Helvetica Neue'
					}
				});

			} else {
				label_numero = Titanium.UI.createButton({
					textAlign : 'center',
					color : 'black',
					width : '30%',
					height : '15%',
					left : l * 30 + '%',
					top : i * 15 + '%',
					id : array_numeros_cientifica[i][l],
					title : array_numeros_cientifica[i][l],
					font : {
						fontSize : 20,
						fontWeight : 'bold',
						fontFamily : 'Helvetica Neue'
					}
				});

			}

			label_numero.addEventListener('click', function(e) {

				if (vista_display_cientifica.value == 0) {
					vista_display_cientifica.value = e.source.id + '';
				} else {
					vista_display_cientifica.value = vista_display_cientifica.value + '' + e.source.id;
				}

				if (e.source.id == 'CE') {

					teclaCE2();
				}

				if (e.source.id == 'pi') {
					teclaPI();
				}

				if (e.source.id == 'e') {

					teclaE();
				}

				if (e.source.id == 'ln2') {

					teclaLN2();
				}

				if (e.source.id == 'ln10') {

					teclaLN10();
				}

				if (e.source.id == 'log2e') {

					teclaLOG2E();
				}

				if (e.source.id == '+/-') {

					if (resultado > 0) {
						resultado = 0 - resultado;

					} else if (resultado < 0) {
						resultado = Math.abs(resultado);
					}

					vista_display_cientifica.value = resultado + '';
				}

				if (operacion == '') {
					operando1 = vista_display_cientifica.value;
				}

				if (e.source.id != 'pi') {
					if ((operacion == '+') || (operacion == '-') || (operacion == '*') || (operacion == '/') || (operacion == 'x^y')) {
						operando2 = operando2 + '' + e.source.id;
					}
				}

				if (e.source.id == '=') {
					vista_display_cientifica.value = vista_display_cientifica.value + '' + e.source.id;
					if (operando1 == Math.PI || operando2 == Math.PI + "=") {
						vista_display_cientifica.value = calcularPI(operando1, operando2, operacion);
					} else {
						vista_display_cientifica.value = calcular(operando1, operando2, operacion);
					}
					operando2 = 0;
					operacion = '';
					operando1 = resultado;
				}

			});
			vista_numeros_cientifica.add(label_numero);
		}
	}

	for (var i = 0; i < array_operaciones_cientifica.length; i++) {
		for (var l = 0; l < array_operaciones_cientifica[i].length; l++) {

			var label_operacion;

			if (array_operaciones_cientifica[i][l] == 'sqrt') {

				label_operacion = Titanium.UI.createButton({
					image : 'images/sqrt.png',
					color : 'black',
					width : '25%',
					height : '15%',
					left : l * 30 + '%',
					top : i * 15 + '%',
					id : array_operaciones_cientifica[i][l]

				});

			} else if (array_operaciones_cientifica[i][l] == 'round') {

				label_operacion = Titanium.UI.createButton({
					textAlign : 'center',
					color : 'black',
					width : '25%',
					height : '15%',
					left : l * 30 + '%',
					top : i * 15 + '%',
					id : array_operaciones_cientifica[i][l],
					title : array_operaciones_cientifica[i][l],
					font : {
						fontSize : 13,
						fontWeight : 'bold',
						fontFamily : 'Helvetica Neue'
					}
				});

			} else {

				label_operacion = Titanium.UI.createButton({
					textAlign : 'center',
					color : 'black',
					width : '25%',
					height : '15%',
					left : l * 30 + '%',
					top : i * 15 + '%',
					id : array_operaciones_cientifica[i][l],
					title : array_operaciones_cientifica[i][l],
					font : {
						fontSize : 20,
						fontWeight : 'bold',
						fontFamily : 'Helvetica Neue'
					}
				});

			}

			label_operacion.addEventListener('click', function(e) {
				if (e.source.id != '=') {

					if (e.source.id == 'x^y') {
						vista_display_cientifica.value = vista_display_cientifica.value + '' + '^';

					} else if (e.source.id == '1/x') {
						vista_display_cientifica.value = calcular(1, operando1, '/');
						operando2 = 0;
						operacion = '';
						operando1 = resultado;
					} else if (e.source.id == 'x^2') {
						vista_display_cientifica.value = calcular(operando1, 2, 'x^y');
						operando2 = 0;
						operacion = '';
						operando1 = resultado;

					} else if (e.source.id == 'x^3') {
						vista_display_cientifica.value = calcular(operando1, 3, 'x^y');
						operando2 = 0;
						operacion = '';
						operando1 = resultado;

					} else if (e.source.id == 'x!') {
						try {
							resultado = factorial(operando1);
							vista_display_cientifica.value = resultado + '';
							operando2 = 0;
							operacion = '';
							operando1 = resultado;
						} catch(err) {
							vista_display_cientifica.value = 'ERROR';
						}

					} else if (e.source.id == 'sqrt') {
						resultado = Math.sqrt(operando1);
						vista_display_cientifica.value = resultado + '';
						operando2 = 0;
						operacion = '';
						operando1 = resultado;

					} else if (e.source.id == 'ln') {
						resultado = Math.log(operando1);
						vista_display_cientifica.value = resultado + '';
						operando2 = 0;
						operacion = '';
						operando1 = resultado;
					} else if (e.source.id == 'exp') {
						resultado = Math.exp(operando1);
						vista_display_cientifica.value = resultado + '';
						operando2 = 0;
						operacion = '';
						operando1 = resultado;
					} else if (e.source.id == 'sin') {
						resultado = Math.sin(operando1);
						vista_display_cientifica.value = resultado + '';
						operando2 = 0;
						operacion = '';
						operando1 = resultado;
					} else if (e.source.id == 'cos') {
						resultado = Math.cos(operando1);
						vista_display_cientifica.value = resultado + '';
						operando2 = 0;
						operacion = '';
						operando1 = resultado;
					} else if (e.source.id == 'tan') {
						resultado = Math.tan(operando1);
						vista_display_cientifica.value = resultado + '';
						operando2 = 0;
						operacion = '';
						operando1 = resultado;
					} else if (e.source.id == 'abs') {
						resultado = Math.abs(operando1);
						vista_display_cientifica.value = resultado + '';
						operando2 = 0;
						operacion = '';
						operando1 = resultado;
					} else if (e.source.id == '%') {
						resultado = operando1 / 100;
						vista_display_cientifica.value = resultado + '';
						operando2 = 0;
						operacion = '';
						operando1 = resultado;
					} else if (e.source.id == 'round') {
						resultado = Math.round(operando1);
						vista_display_cientifica.value = resultado + '';
						operando2 = 0;
						operacion = '';
						operando1 = resultado;
					} else {

						vista_display_cientifica.value = vista_display_cientifica.value + '' + e.source.id;
					}
					operacion = e.source.id;

					if (operando1 != '0' && operando2 != '0') {
						vista_display_cientifica.value = calcular(operando1, operando2, operacion);
						operando2 = 0;
						operacion = '';
						operando1 = resultado;
					}

				}

			});
			vista_operaciones_cientifica.add(label_operacion);
		}
	}
})();

cientifica.add(vista_operaciones_cientifica);
cientifica.add(vista_display_cientifica);
cientifica.add(vista_numeros_cientifica);

var boton = Titanium.UI.createButton({
	textAlign : 'center',
	color : 'black',
	width : '90%',
	height : '20%'
});

var tab2 = Titanium.UI.createTab({
	title : 'Científica',
	window : cientifica
});

//  añadir pestañas
tabGroup.addTab(tab1);
tabGroup.addTab(tab2);

// abrir grupo de pestañas
tabGroup.open();
