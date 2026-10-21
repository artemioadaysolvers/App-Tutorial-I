import * as THREE from './vendor/three/three.module.js';
import {OrbitControls} from './vendor/three/OrbitControls.js';
import {GLTFLoader} from './vendor/three/GLTFLoader.js';

export const MODEL_CONFIG={url:'./models/casio_fx2200.glb',rotation:[0,0,0],exposure:1.15};
export async function createViewer(container,status){
  const renderer=new THREE.WebGLRenderer({antialias:true,alpha:true,powerPreference:'low-power'});
  renderer.setPixelRatio(Math.min(devicePixelRatio||1,1.5));
  renderer.outputColorSpace=THREE.SRGBColorSpace;renderer.toneMapping=THREE.ACESFilmicToneMapping;renderer.toneMappingExposure=MODEL_CONFIG.exposure;
  renderer.setClearColor(0,0);container.append(renderer.domElement);
  const scene=new THREE.Scene();
  const camera=new THREE.PerspectiveCamera(34,1,.01,120);
  const controls=new OrbitControls(camera,renderer.domElement);
  controls.enableDamping=true;controls.dampingFactor=.09;controls.enablePan=false;controls.minDistance=3;controls.maxDistance=18;
  controls.autoRotateSpeed=.6;controls.maxPolarAngle=Math.PI;
  scene.add(new THREE.HemisphereLight(0xfff9ea,0x847c6b,3));
  const keyLight=new THREE.DirectionalLight(0xffffff,3.5);keyLight.position.set(2,5,5);scene.add(keyLight);
  const fill=new THREE.DirectionalLight(0xfff2dd,2);fill.position.set(-4,1,-3);scene.add(fill);
  const object=new THREE.Group();scene.add(object);
  let frame=0,running=false,dirty=true,last=0,loaded=false;
  controls.addEventListener('change',()=>dirty=true);
  function reset(){
    // Fit every bounding-box corner into the narrower field of view, at any aspect ratio.
    const direction=new THREE.Vector3(.03,.15,1).normalize();
    const right=new THREE.Vector3().crossVectors(new THREE.Vector3(0,1,0),direction).normalize();
    const up=new THREE.Vector3().crossVectors(direction,right).normalize();
    const bounds=new THREE.Box3().setFromObject(object);
    let distance=8;
    if(!bounds.isEmpty()){
      distance=0;
      const tangent=Math.tan(THREE.MathUtils.degToRad(camera.fov/2));
      for(const x of [bounds.min.x,bounds.max.x])for(const y of [bounds.min.y,bounds.max.y])for(const z of [bounds.min.z,bounds.max.z]){
        const p=new THREE.Vector3(x,y,z),depth=p.dot(direction);
        distance=Math.max(distance,depth+Math.abs(p.dot(right))/(tangent*camera.aspect),depth+Math.abs(p.dot(up))/tangent);
      }
      distance*=1.22;
    }
    controls.maxDistance=Math.max(18,distance*2);controls.minDistance=Math.min(3,distance*.4);
    camera.position.copy(direction).multiplyScalar(distance);
    controls.target.set(0,0,0);controls.update();dirty=true;
  }
  function resize(){
    const width=container.clientWidth,height=container.clientHeight;
    if(!width||!height)return;
    renderer.setSize(width,height,false);camera.aspect=width/height;camera.updateProjectionMatrix();reset();dirty=true;
  }
  new ResizeObserver(resize).observe(container);resize();reset();
  function draw(t){
    if(!running)return;
    if(t-last>30){controls.update();if(dirty||controls.autoRotate){renderer.render(scene,camera);dirty=false;}last=t;}
    frame=requestAnimationFrame(draw);
  }
  function resume(){if(!running){running=true;dirty=true;last=0;frame=requestAnimationFrame(draw);}}
  function pause(){running=false;cancelAnimationFrame(frame);}
  renderer.domElement.addEventListener('webglcontextlost',e=>{e.preventDefault();pause();status('Contexto gráfico interrumpido. El cálculo 2D sigue disponible.');});
  renderer.domElement.addEventListener('webglcontextrestored',()=>{dirty=true;status(loaded?'CASIO fx-2200 / modelo local':'Cargando modelo local…');resume();});
  status('Cargando Casio_fx2200.glb…');
  new GLTFLoader().load(MODEL_CONFIG.url,gltf=>{
    const model=gltf.scene;model.rotation.set(...MODEL_CONFIG.rotation);model.updateMatrixWorld(true);
    let box=new THREE.Box3().setFromObject(model),size=box.getSize(new THREE.Vector3());
    const longest=Math.max(size.x,size.y,size.z);
    if(!Number.isFinite(longest)||longest<=0){status('El archivo GLB no contiene geometría visible.');return;}
    model.scale.multiplyScalar(4/longest);model.updateMatrixWorld(true);
    box=new THREE.Box3().setFromObject(model);model.position.sub(box.getCenter(new THREE.Vector3()));
    object.add(model);loaded=true;reset();dirty=true;status('CASIO fx-2200 / modelo local');
  },progress=>{if(progress.total)status('Cargando modelo local: '+Math.round(progress.loaded/progress.total*100)+' %.');},error=>{console.error('Carga GLB',error);status('No se pudo cargar el modelo. La calculadora 2D sigue disponible.');});
  return {reset,resume,pause,rotate(on){controls.autoRotate=on;dirty=true;}};
}
