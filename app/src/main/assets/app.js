// Presentation and native transport. Arithmetic is implemented in CalculatorEngine.kt.
const $ = s => document.querySelector(s);
const native = window.NativeApp;
let activePage='intro', viewer, viewerTask, sequence=0, state, toastTimer, manualIndex=0;
const pending = new Map();
let keys=Promise.resolve();
if(native) native.onmessage=event=>{
  try {
    const response=JSON.parse(event.data), task=pending.get(response.id);
    if(!task)return;
    clearTimeout(task.timer); pending.delete(response.id);
    response.ok ? task.resolve(response) : task.reject(new Error(response.error));
  } catch(error){console.error('Respuesta nativa',error);}
};
async function request(action,data={}){
  if(!native) throw new Error('Abra el APK Android para efectuar cálculos.');
  const id=++sequence;
  const response=await new Promise((resolve,reject)=>{
    const timer=setTimeout(()=>{pending.delete(id);reject(new Error('El instrumento no responde. Vuelva a abrir la aplicación.'));},8000);
    pending.set(id,{resolve,reject,timer});
    native.postMessage(JSON.stringify({id,action,...data}));
  });
  if(response.state) paint(response.state);
  return response;
}
async function safe(action,data){try{return await request(action,data);}catch(error){toast(error.message);$('#calculation-notice').textContent=error.message;}}
function toast(message){$('#toast').textContent=message;$('#toast').classList.add('visible');clearTimeout(toastTimer);toastTimer=setTimeout(()=>$('#toast').classList.remove('visible'),3500);}
function feedback(){if(native&&state?.sound!==false)request('feedback').catch(()=>{});}
document.addEventListener('pointerdown',event=>{if(event.isPrimary&&event.button===0&&event.target.closest('button'))feedback();},{capture:true,passive:true});
document.addEventListener('click',event=>{if(event.detail===0&&event.target.closest('button'))feedback();},true);
function paint(next){
  state=next;
  $('#display').textContent=state.display;
  $('#display').classList.toggle('long',state.display.length>12);
  $('#mode-indicator').textContent=state.powered?state.mode:'';
  $('#memory-indicator').textContent=state.powered&&state.memory!=='0'?'M':'';
  $('#prefix-indicator').textContent=state.powered?[state.inverse?'INV':'',state.hyper?'HYP':''].filter(Boolean).join(' '):'';
  $('#pending-indicator').textContent=state.powered?state.pending:'';
  $('#memory-value').textContent=state.memory;
  $('#stat-count').textContent=state.count;
  $('#calculation-notice').textContent=state.powered?state.notice:'Instrumento apagado. Pulse I/O para encender.';
  $('#power').setAttribute('aria-pressed',String(state.powered));
  for(const [key,enabled] of [['INV',state.inverse],['HYP',state.hyper]])document.querySelector(`[data-key="${key}"]`).setAttribute('aria-pressed',String(enabled));
  $('#haptic-toggle').textContent='Respuesta táctil: '+(state.haptics?'sí':'no');$('#haptic-toggle').setAttribute('aria-pressed',String(state.haptics));
  $('#sound-toggle').textContent='Sonido: '+(state.sound?'sí':'no');$('#sound-toggle').setAttribute('aria-pressed',String(state.sound));
  for(const cell of document.querySelectorAll('[data-stat]')) {
    const key=cell.querySelector('button');
    cell.querySelector('.key-label').textContent=state.mode==='SD'?cell.dataset.stat:cell.dataset.normal;
    key.setAttribute('aria-label',state.mode==='SD'?cell.dataset.statName:key.dataset.normalName);
  }
}
function fitMachine(){const box=$('#machine-stage'),machine=$('#machine');if(box.clientWidth&&box.clientHeight)machine.style.setProperty('--machine-scale',Math.min((box.clientWidth-10)/machine.offsetWidth,(box.clientHeight-10)/machine.offsetHeight,1.18));}
new ResizeObserver(fitMachine).observe($('#machine-stage'));
async function go(page){
  if(!['intro','exhibit','calculator'].includes(page))return;
  activePage=page;
  document.querySelectorAll('.page').forEach(p=>p.hidden=p.id!==page);
  document.querySelectorAll('.tabs button').forEach(b=>{if(b.dataset.page===page)b.setAttribute('aria-current','page');else b.removeAttribute('aria-current');});
  $('#page-number').textContent='HOJA 0'+(['intro','exhibit','calculator'].indexOf(page)+1)+' / 03';
  viewer?.pause();
  if(page==='calculator'){fitMachine();if(native)await safe('state');return;}
  if(page!=='exhibit')return;
  if(!viewerTask) viewerTask=import('./scene.js').then(({createViewer})=>createViewer($('#scene'),message=>$('#model-status').textContent=message)).then(v=>viewer=v).catch(error=>{console.error(error);$('#model-status').textContent='No se pudo iniciar el visor 3D. La calculadora sigue disponible.';});
  await viewerTask;
  if(activePage==='exhibit')viewer?.resume();
}
document.querySelectorAll('[data-page]').forEach(b=>b.addEventListener('click',()=>go(b.dataset.page)));
window.appBack=()=>{
  for(const id of ['#settings-dialog','#help-dialog','#sources-dialog'])if($(id).open){$(id).close();return true;}
  if(activePage==='calculator'){go('exhibit');return true;}
  if(activePage==='exhibit'){go('intro');return true;}
  return false;
};
const scientific=[
  ['MODE','MODE','','mode-cell','Selector de unidad angular y modo estadístico'],['INV','arc','INV','','Funciones inversas y secundarias'],['LOG','log','10ˣ','','Logaritmo decimal'],['LN','ln','eˣ','','Logaritmo natural'],['POW','xʸ','x¹/ʸ','','Potencia; INV para raíz enésima'],
  ['SIGN','+/−','','','Cambiar signo'],['DMS','° ′ ″','←','','Grados, minutos y segundos; INV para ver sexagesimal'],['HYP','hyp','','','Funciones hiperbólicas'],['SIN','sin','','','Seno; INV para arcoseno'],['COS','cos','','','Coseno; INV para arcocoseno'],['TAN','tan','','','Tangente; INV para arcotangente'],
  ['FRAC','a b/c','Σx²','','Introducir fracción'],['RECIP','1/x','Σx','','Recíproco'],['FACT','x!','n','','Factorial'],['SQRT','√','x̄','','Raíz cuadrada'],['MIN','M in','σn−1','','Guardar en memoria'],['MR','MR','σn','','Recuperar memoria']
];
const numeric=[['7','7'],['8','8'],['9','9'],['C','C','','clear-key'],['AC','AC','','clear-key'],['4','4'],['5','5'],['6','6'],['*','×'],['/','÷'],['1','1'],['2','2'],['3','3'],['+','+'],['-','−'],['0','0'],['.','.'],['EXP','EXP','π'],['=','='],['MPLUS','M+','SD: DATA / DEL']];
const statNames={FRAC:['Σx²','Suma de cuadrados'],RECIP:['Σx','Suma de datos'],FACT:['n','Número de datos'],SQRT:['x̄','Media aritmética'],MIN:['σn−1','Desviación típica muestral'],MR:['σn','Desviación típica poblacional']};
function keyButton(key,label,secondary='',classes='',name=label){
  const cell=document.createElement('div');cell.className='key-cell '+(classes==='mode-cell'?classes:'');
  const top=document.createElement('span');top.className='key-label';top.textContent=label;
  const button=document.createElement('button');button.className='key '+(classes!=='mode-cell'?classes:'');button.dataset.key=key;button.dataset.normalName=name;button.setAttribute('aria-label',name);
  button.textContent=classes==='mode-cell'?'':label;
  if(key==='INV'||key==='HYP')button.setAttribute('aria-pressed','false');
  const lower=document.createElement('span');lower.className='key-secondary';lower.textContent=secondary;
  cell.append(top,button,lower);
  if(key==='MODE'){const legend=document.createElement('span');legend.className='mode-legend';legend.textContent='RAD\nDEG\nGRA / SD';legend.style.whiteSpace='pre';cell.prepend(legend);}
  if(statNames[key]){cell.dataset.normal=label;cell.dataset.stat=statNames[key][0];cell.dataset.statName=statNames[key][1];}
  return cell;
}
scientific.forEach(k=>$('#scientific-keys').append(keyButton(...k)));
numeric.forEach(k=>$('#numeric-keys').append(keyButton(...k)));
function press(key){keys=keys.then(()=>safe('key',{key}));return keys;}
document.querySelectorAll('[data-key]').forEach(b=>b.addEventListener('click',()=>press(b.dataset.key)));
$('#reset-camera').addEventListener('click',()=>viewer?.reset());
document.addEventListener('keydown',event=>{
  if(activePage!=='calculator'||document.querySelector('dialog[open]')||event.ctrlKey||event.metaKey||event.altKey||['SELECT','INPUT','TEXTAREA'].includes(event.target.tagName))return;
  if(event.target.tagName==='BUTTON'&&['Enter',' '].includes(event.key))return;
  const key=/^[0-9.+\-*/=]$/.test(event.key)?event.key:({Enter:'=',Backspace:'BACKSPACE',Escape:'AC',Delete:'C',',':'.',e:'EXP',F9:'SIGN'})[event.key];
  if(key){event.preventDefault();feedback();press(key);}
});
const manual=[
  ['Operaciones','Operaciones y corrección',`<p>Las cuatro operaciones se resuelven en el orden de pulsación. Una nueva operación ejecuta la anterior.</p><p class="manual-example">2 + 3 × 4 = → 20<br>9 √ → 3<br>5 x! → 120</p><p><b>C</b> borra la entrada. <b>AC</b> borra el cálculo pendiente; conserva la memoria M y los datos SD. I/O apaga o enciende.</p><p>Teclado físico: Enter =, Escape AC, Suprimir C, Retroceso corrige, E introduce exponente y F9 cambia el signo.</p>`],
  ['Funciones científicas','Unidades y funciones',`<p>MODE recorre RAD → DEG → GRA → SD. Pulse MODE en la calculadora para seleccionar el modo.</p><p class="manual-example">DEG: 30 sin → 0.5<br>DEG: 0.5 INV sin → 30<br>2 INV log → 100<br>27 INV xʸ 3 = → 3</p><p>INV activa 10ˣ, eˣ, raíz enésima y π (INV EXP). Se consume al ejecutar la función. hyp selecciona funciones hiperbólicas; INV hyp permite sus inversas.</p><p>EXP introduce el exponente de base diez. Ejemplo: 2 EXP +/− 3 → 0.002.</p>`],
  ['Fracción','Fracciones y ángulos',`<p>La tecla a b/c separa numerador y denominador. Una segunda pulsación permite introducir un número mixto.</p><p class="manual-example">1 a b/c 2 = → 0.5<br>2 a b/c 1 a b/c 3 = → 2.3333333</p><p>Los resultados de fracciones se muestran en decimal. Para un ángulo sexagesimal introduzca grados, minutos y segundos, pulsando ° ′ ″ después de cada componente.</p><p class="manual-example">12 °′″ 30 °′″ 0 °′″ → 12.5<br>12.5 INV °′″ → 12°30′0″</p>`],
  ['Memoria','Registro independiente M',`<p>M in guarda el número mostrado. MR lo recupera. M+ suma el valor a la memoria. El indicador M señala una memoria distinta de cero.</p><p class="manual-example">8 M in AC 4 M+ MR → 12<br>0 M in → borra M</p><p>Para restar una cantidad de la memoria, cambie su signo antes de M+. Puede borrar M desde Ajustes.</p><p>La memoria y los cálculos se conservan al cambiar entre las tres pantallas. Al recrear o cerrar el proceso Android se reinician; el modo angular, sonido y tacto sí se guardan.</p>`],
  ['SD','Estadística de una variable',`<p>Seleccione SD. Introduzca cada dato y pulse M+. INV M+ retira una coincidencia del valor mostrado. Las seis teclas inferiores del bloque científico toman las funciones estadísticas inscritas debajo.</p><p class="manual-example">2 M+ 4 M+ 6 M+<br>n → 3 · Σx → 12 · Σx² → 56<br>x̄ → 4 · σn−1 → 2</p><p>σn calcula la desviación poblacional; σn−1, la muestral. No se admite la desviación muestral con menos de dos datos.</p><p>Use «Borrar datos SD» en Ajustes para iniciar otra serie. AC conserva la serie.</p>`]
];
const sourcesMarkup=`<div class="source-list"><p><b>[1]</b> Publicidad de 1978: <a href="https://www.worldradiohistory.com/Archive-Practical-Electronics/70s/Practical-Electronics-1978-06.pdf">Practical Electronics, junio, p. 775.</a></p><p><b>[2]</b> Ejemplar y teclado: <a href="https://casio.ledudu.com/pockets.asp?type=1345">colección Ledudu, fx-2200.</a> <a href="https://casio.ledudu.com/pockets.asp?type=1029">fx-2000 y portada de su manual.</a></p><p><b>[3]</b> Procedimientos científicos: <a href="https://casio.ledudu.com/images/calculs/casio/manuels/fx911.pdf">manual original Casio fx-911, §6.</a> Referencia de operaciones, de otro modelo.</p></div><small>La fotografía del ejemplar se utilizó para identificar el teclado. El manual fx-911 corresponde a otro modelo. No se localizó una copia íntegra verificable del manual específico de la fx-2200.</small>`;
function showManual(index=0){
  manualIndex=Math.max(0,Math.min(manual.length-1,index));
  $('#manual-content').innerHTML='<h3>'+manual[manualIndex][1]+'</h3>'+manual[manualIndex][2];
  $('#manual-page').textContent=String(manualIndex+1).padStart(2,'0')+' / '+String(manual.length).padStart(2,'0');
  $('#manual-prev').disabled=manualIndex===0;$('#manual-next').disabled=manualIndex===manual.length-1;
  document.querySelectorAll('#manual-tabs button').forEach((b,i)=>b.setAttribute('aria-pressed',String(i===manualIndex)));
}
manual.forEach(([label],i)=>{const b=document.createElement('button');b.textContent=label;b.addEventListener('click',()=>showManual(i));$('#manual-tabs').append(b);});
function openManual(i=0){showManual(i);$('#help-dialog').showModal();}
$('#help-button').addEventListener('click',()=>openManual());
$('#sources-button').addEventListener('click',()=>{$('#sources-content').innerHTML=sourcesMarkup;$('#sources-dialog').showModal();});
$('#close-sources').addEventListener('click',()=>$('#sources-dialog').close());
$('#close-help').addEventListener('click',()=>$('#help-dialog').close());$('#manual-prev').addEventListener('click',()=>showManual(manualIndex-1));$('#manual-next').addEventListener('click',()=>showManual(manualIndex+1));
// External documents are opened by Kotlin only after the reader follows a cited link.
for(const id of ['#manual-content','#sources-content'])$(id).addEventListener('click',event=>{const a=event.target.closest('a');if(!a)return;event.preventDefault();if(native)safe('source',{url:a.href});else window.open(a.href,'_blank','noopener');});
$('#settings-button').addEventListener('click',()=>$('#settings-dialog').showModal());$('#close-settings').addEventListener('click',()=>$('#settings-dialog').close());
$('#haptic-toggle').addEventListener('click',()=>safe('settings',{haptics:!state?.haptics}));$('#sound-toggle').addEventListener('click',()=>safe('settings',{sound:!state?.sound}));
$('#copy-result').addEventListener('click',()=>{keys=keys.then(async()=>{if(await safe('copy'))toast('Resultado copiado.');});});$('#share-result').addEventListener('click',()=>{keys=keys.then(()=>safe('share'));});
for(const id of ['#clear-stat','#mobile-clear-stat'])$(id).addEventListener('click',()=>press('SAC'));
for(const id of ['#clear-memory','#mobile-clear-memory'])$(id).addEventListener('click',()=>press('MC'));
document.addEventListener('visibilitychange',()=>{if(document.hidden)viewer?.pause();else if(activePage==='exhibit')viewer?.resume();});
if(native)safe('state');else $('#calculation-notice').textContent='Vista previa. Abra el APK Android para efectuar cálculos.';
