import"construct-style-sheets-polyfill";import{svg as z,css as g,LitElement as $,html as l,render as Xe,nothing as Ie}from"lit";import{property as p,state as f,customElement as k,query as Z}from"lit/decorators.js";import{classMap as Ze}from"lit/directives/class-map.js";import{literal as ee,html as lt}from"lit/static-html.js";const dt="modulepreload",ct=function(t){return"/"+t},Ae={},u=function(e,o,s){if(!o||o.length===0)return e();const i=document.getElementsByTagName("link");return Promise.all(o.map(r=>{if(r=ct(r),r in Ae)return;Ae[r]=!0;const n=r.endsWith(".css"),a=n?'[rel="stylesheet"]':"";if(!!s)for(let m=i.length-1;m>=0;m--){const w=i[m];if(w.href===r&&(!n||w.rel==="stylesheet"))return}else if(document.querySelector(`link[href="${r}"]${a}`))return;const c=document.createElement("link");if(c.rel=n?"stylesheet":dt,n||(c.as="script",c.crossOrigin=""),c.href=r,document.head.appendChild(c),n)return new Promise((m,w)=>{c.addEventListener("load",m),c.addEventListener("error",()=>w(new Error(`Unable to preload CSS for ${r}`)))})})).then(()=>e())};function ht(t){var o;const e=[];for(;t&&t.parentNode;){const s=pt(t);if(s.nodeId!==-1){if((o=s.element)!=null&&o.tagName.startsWith("FLOW-CONTAINER-"))break;e.push(s)}t=t.parentElement?t.parentElement:t.parentNode.host}return e.reverse()}function pt(t){const e=window.Vaadin;if(e&&e.Flow){const{clients:o}=e.Flow,s=Object.keys(o);for(const i of s){const r=o[i];if(r.getNodeId){const n=r.getNodeId(t);if(n>=0)return{nodeId:n,uiId:r.getUIId(),element:t}}}}return{nodeId:-1,uiId:-1,element:void 0}}function ut(t,e){if(t.contains(e))return!0;let o=e;const s=e.ownerDocument;for(;o&&o!==s&&o!==t;)o=o.parentNode||(o instanceof ShadowRoot?o.host:null);return o===t}const mt=(t,e)=>{const o=t[e];return o?typeof o=="function"?o():Promise.resolve(o):new Promise((s,i)=>{(typeof queueMicrotask=="function"?queueMicrotask:setTimeout)(i.bind(null,new Error("Unknown variable dynamic import: "+e)))})};var y=(t=>(t.text="text",t.checkbox="checkbox",t.range="range",t.color="color",t))(y||{});const T={lumoSize:["--lumo-size-xs","--lumo-size-s","--lumo-size-m","--lumo-size-l","--lumo-size-xl"],lumoSpace:["--lumo-space-xs","--lumo-space-s","--lumo-space-m","--lumo-space-l","--lumo-space-xl"],lumoBorderRadius:["0","--lumo-border-radius-m","--lumo-border-radius-l"],lumoFontSize:["--lumo-font-size-xxs","--lumo-font-size-xs","--lumo-font-size-s","--lumo-font-size-m","--lumo-font-size-l","--lumo-font-size-xl","--lumo-font-size-xxl","--lumo-font-size-xxxl"],lumoTextColor:["--lumo-header-text-color","--lumo-body-text-color","--lumo-secondary-text-color","--lumo-tertiary-text-color","--lumo-disabled-text-color","--lumo-primary-text-color","--lumo-error-text-color","--lumo-success-text-color"],basicBorderSize:["0px","1px","2px","3px"]},vt=Object.freeze(Object.defineProperty({__proto__:null,presets:T},Symbol.toStringTag,{value:"Module"})),F={textColor:{propertyName:"color",displayName:"Text color",editorType:y.color,presets:T.lumoTextColor},fontSize:{propertyName:"font-size",displayName:"Font size",editorType:y.range,presets:T.lumoFontSize,icon:"font"},fontWeight:{propertyName:"font-weight",displayName:"Bold",editorType:y.checkbox,checkedValue:"bold"},fontStyle:{propertyName:"font-style",displayName:"Italic",editorType:y.checkbox,checkedValue:"italic"}},U={backgroundColor:{propertyName:"background-color",displayName:"Background color",editorType:y.color},borderColor:{propertyName:"border-color",displayName:"Border color",editorType:y.color},borderWidth:{propertyName:"border-width",displayName:"Border width",editorType:y.range,presets:T.basicBorderSize,icon:"square"},borderRadius:{propertyName:"border-radius",displayName:"Border radius",editorType:y.range,presets:T.lumoBorderRadius,icon:"square"},padding:{propertyName:"padding",displayName:"Padding",editorType:y.range,presets:T.lumoSpace,icon:"square"},gap:{propertyName:"gap",displayName:"Spacing",editorType:y.range,presets:T.lumoSpace,icon:"square"}},gt={height:{propertyName:"height",displayName:"Size",editorType:y.range,presets:T.lumoSize,icon:"square"},paddingInline:{propertyName:"padding-inline",displayName:"Padding",editorType:y.range,presets:T.lumoSpace,icon:"square"}},ft={iconColor:{propertyName:"color",displayName:"Icon color",editorType:y.color,presets:T.lumoTextColor},iconSize:{propertyName:"font-size",displayName:"Icon size",editorType:y.range,presets:T.lumoFontSize,icon:"font"}},bt=Object.freeze(Object.defineProperty({__proto__:null,fieldProperties:gt,iconProperties:ft,shapeProperties:U,textProperties:F},Symbol.toStringTag,{value:"Module"}));function Qe(t){const e=t.charAt(0).toUpperCase()+t.slice(1);return{tagName:t,displayName:e,elements:[{selector:t,displayName:"Element",properties:[U.backgroundColor,U.borderColor,U.borderWidth,U.borderRadius,U.padding,F.textColor,F.fontSize,F.fontWeight,F.fontStyle]}]}}const yt=Object.freeze(Object.defineProperty({__proto__:null,createGenericMetadata:Qe},Symbol.toStringTag,{value:"Module"})),wt=t=>mt(Object.assign({"./components/defaults.ts":()=>u(()=>Promise.resolve().then(()=>bt),void 0),"./components/generic.ts":()=>u(()=>Promise.resolve().then(()=>yt),void 0),"./components/presets.ts":()=>u(()=>Promise.resolve().then(()=>vt),void 0),"./components/vaadin-app-layout.ts":()=>u(()=>import("./assets/vaadin-app-layout-37492a04.js"),[]),"./components/vaadin-avatar.ts":()=>u(()=>import("./assets/vaadin-avatar-7047be31.js"),[]),"./components/vaadin-big-decimal-field.ts":()=>u(()=>import("./assets/vaadin-big-decimal-field-b42c1de1.js"),["assets/vaadin-big-decimal-field-b42c1de1.js","assets/vaadin-text-field-e82c445d.js"]),"./components/vaadin-button.ts":()=>u(()=>import("./assets/vaadin-button-79ad9d5f.js"),[]),"./components/vaadin-checkbox-group.ts":()=>u(()=>import("./assets/vaadin-checkbox-group-a9a9e85d.js"),["assets/vaadin-checkbox-group-a9a9e85d.js","assets/vaadin-text-field-e82c445d.js","assets/vaadin-checkbox-13797fc9.js"]),"./components/vaadin-checkbox.ts":()=>u(()=>import("./assets/vaadin-checkbox-13797fc9.js"),[]),"./components/vaadin-combo-box.ts":()=>u(()=>import("./assets/vaadin-combo-box-9046f78f.js"),["assets/vaadin-combo-box-9046f78f.js","assets/vaadin-text-field-e82c445d.js"]),"./components/vaadin-email-field.ts":()=>u(()=>import("./assets/vaadin-email-field-da851bcb.js"),["assets/vaadin-email-field-da851bcb.js","assets/vaadin-text-field-e82c445d.js"]),"./components/vaadin-horizontal-layout.ts":()=>u(()=>import("./assets/vaadin-horizontal-layout-f7b1ab51.js"),[]),"./components/vaadin-integer-field.ts":()=>u(()=>import("./assets/vaadin-integer-field-6e2954cf.js"),["assets/vaadin-integer-field-6e2954cf.js","assets/vaadin-text-field-e82c445d.js"]),"./components/vaadin-menu-bar.ts":()=>u(()=>import("./assets/vaadin-menu-bar-be33385c.js"),[]),"./components/vaadin-number-field.ts":()=>u(()=>import("./assets/vaadin-number-field-31df11f5.js"),["assets/vaadin-number-field-31df11f5.js","assets/vaadin-text-field-e82c445d.js"]),"./components/vaadin-password-field.ts":()=>u(()=>import("./assets/vaadin-password-field-49ffb113.js"),["assets/vaadin-password-field-49ffb113.js","assets/vaadin-text-field-e82c445d.js"]),"./components/vaadin-progress-bar.ts":()=>u(()=>import("./assets/vaadin-progress-bar-3b53bb70.js"),[]),"./components/vaadin-radio-group.ts":()=>u(()=>import("./assets/vaadin-radio-group-4a6e2cf4.js"),["assets/vaadin-radio-group-4a6e2cf4.js","assets/vaadin-text-field-e82c445d.js"]),"./components/vaadin-scroller.ts":()=>u(()=>import("./assets/vaadin-scroller-35e68818.js"),[]),"./components/vaadin-select.ts":()=>u(()=>import("./assets/vaadin-select-5d6ab45b.js"),["assets/vaadin-select-5d6ab45b.js","assets/vaadin-text-field-e82c445d.js"]),"./components/vaadin-split-layout.ts":()=>u(()=>import("./assets/vaadin-split-layout-10c9713b.js"),[]),"./components/vaadin-text-area.ts":()=>u(()=>import("./assets/vaadin-text-area-41c5f60c.js"),["assets/vaadin-text-area-41c5f60c.js","assets/vaadin-text-field-e82c445d.js"]),"./components/vaadin-text-field.ts":()=>u(()=>import("./assets/vaadin-text-field-e82c445d.js"),[]),"./components/vaadin-time-picker.ts":()=>u(()=>import("./assets/vaadin-time-picker-2fa5314f.js"),["assets/vaadin-time-picker-2fa5314f.js","assets/vaadin-text-field-e82c445d.js"]),"./components/vaadin-vertical-layout.ts":()=>u(()=>import("./assets/vaadin-vertical-layout-ff73c403.js"),[]),"./components/vaadin-virtual-list.ts":()=>u(()=>import("./assets/vaadin-virtual-list-62d4499a.js"),[])}),`./components/${t}.ts`);class xt{constructor(e=wt){this.loader=e,this.metadata={}}async getMetadata(e){var i;const o=(i=e.element)==null?void 0:i.localName;if(!o)return null;if(!o.startsWith("vaadin-"))return Qe(o);let s=this.metadata[o];if(s)return s;try{s=(await this.loader(o)).default,this.metadata[o]=s}catch{console.warn(`Failed to load metadata for component: ${o}`)}return s||null}}const _t=new xt,se={crosshair:z`<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
   <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
   <path d="M4 8v-2a2 2 0 0 1 2 -2h2"></path>
   <path d="M4 16v2a2 2 0 0 0 2 2h2"></path>
   <path d="M16 4h2a2 2 0 0 1 2 2v2"></path>
   <path d="M16 20h2a2 2 0 0 0 2 -2v-2"></path>
   <path d="M9 12l6 0"></path>
   <path d="M12 9l0 6"></path>
</svg>`,square:z`<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="currentColor" stroke-linecap="round" stroke-linejoin="round">
   <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
   <path d="M3 3m0 2a2 2 0 0 1 2 -2h14a2 2 0 0 1 2 2v14a2 2 0 0 1 -2 2h-14a2 2 0 0 1 -2 -2z"></path>
</svg>`,font:z`<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
   <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
   <path d="M4 20l3 0"></path>
   <path d="M14 20l7 0"></path>
   <path d="M6.9 15l6.9 0"></path>
   <path d="M10.2 6.3l5.8 13.7"></path>
   <path d="M5 20l6 -16l2 0l7 16"></path>
</svg>`,undo:z`<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
   <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
   <path d="M9 13l-4 -4l4 -4m-4 4h11a4 4 0 0 1 0 8h-1"></path>
</svg>`,redo:z`<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
   <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
   <path d="M15 13l4 -4l-4 -4m4 4h-11a4 4 0 0 0 0 8h1"></path>
</svg>`,cross:z`<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" stroke-width="3" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
   <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
   <path d="M18 6l-12 12"></path>
   <path d="M6 6l12 12"></path>
</svg>`};var W=(t=>(t.disabled="disabled",t.enabled="enabled",t.missing_theme="missing_theme",t))(W||{}),E=(t=>(t.local="local",t.global="global",t))(E||{});function ve(t,e){return`${t}|${e}`}class A{constructor(e){this._properties={},this._metadata=e}get metadata(){return this._metadata}get properties(){return Object.values(this._properties)}getPropertyValue(e,o){return this._properties[ve(e,o)]||null}updatePropertyValue(e,o,s,i){if(!s){delete this._properties[ve(e,o)];return}let r=this.getPropertyValue(e,o);r?(r.value=s,r.modified=i||!1):(r={elementSelector:e,propertyName:o,value:s,modified:i||!1},this._properties[ve(e,o)]=r)}addPropertyValues(e){e.forEach(o=>{this.updatePropertyValue(o.elementSelector,o.propertyName,o.value,o.modified)})}getPropertyValuesForElement(e){return this.properties.filter(o=>o.elementSelector===e)}static combine(...e){if(e.length<2)throw new Error("Must provide at least two themes");const o=new A(e[0].metadata);return e.forEach(s=>o.addPropertyValues(s.properties)),o}static fromServerRules(e,o,s){const i=new A(e);return e.elements.forEach(r=>{const n=H(r,o),a=s.find(d=>d.selector===n);a&&r.properties.forEach(d=>{const c=a.properties[d.propertyName];c&&i.updatePropertyValue(r.selector,d.propertyName,c,!0)})}),i}}function H(t,e){const o=t.selector;if(e.themeScope==="global")return o;if(!e.localClassName)throw new Error("Can not build local scoped selector without instance class name");const s=o.match(/^[\w\d-_]+/),i=s&&s[0];if(!i)throw new Error(`Selector does not start with a tag name: ${o}`);return`${i}.${e.localClassName}${o.substring(i.length,o.length)}`}function Ct(t,e,o,s){const i=H(t,e),r={[o]:s};return o==="border-width"&&(parseInt(s)>0?r["border-style"]="solid":r["border-style"]=""),{selector:i,properties:r}}function Et(t){const e=Object.entries(t.properties).map(([o,s])=>`${o}: ${s};`).join(" ");return`${t.selector} { ${e} }`}let te,Pe="";function Ce(t){te||(te=new CSSStyleSheet,document.adoptedStyleSheets=[...document.adoptedStyleSheets,te]),Pe+=t.cssText,te.replaceSync(Pe)}const et=g`
  .editor-row {
    display: flex;
    align-items: baseline;
    padding: var(--theme-editor-section-horizontal-padding);
    gap: 10px;
  }

  .editor-row > .label {
    flex: 0 0 auto;
    width: 120px;
  }

  .editor-row > .editor {
    flex: 1 1 0;
  }
`,Oe="__vaadin-theme-editor-measure-element",Re=/((::before)|(::after))$/,Le=/::part\(([\w\d_-]+)\)$/;Ce(g`
  .__vaadin-theme-editor-measure-element {
    position: absolute;
    top: 0;
    left: 0;
    visibility: hidden;
  }
`);async function kt(t){const e=new A(t),o=document.createElement(t.tagName);o.classList.add(Oe),document.body.append(o),t.setupElement&&await t.setupElement(o);const s={themeScope:E.local,localClassName:Oe};try{t.elements.forEach(i=>{Ve(o,i,s,!0);let r=H(i,s);const n=r.match(Re);r=r.replace(Re,"");const a=r.match(Le),d=r.replace(Le,"");let c=document.querySelector(d);if(c&&a){const V=`[part~="${a[1]}"]`;c=c.shadowRoot.querySelector(V)}if(!c)return;c.style.transition="none";const m=n?n[1]:null,w=getComputedStyle(c,m);i.properties.forEach(L=>{const V=w.getPropertyValue(L.propertyName)||L.defaultValue||"";e.updatePropertyValue(i.selector,L.propertyName,V)}),Ve(o,i,s,!1)})}finally{try{t.cleanupElement&&await t.cleanupElement(o)}finally{o.remove()}}return e}function Ve(t,e,o,s){if(e.stateAttribute){if(e.stateElementSelector){const i=H({...e,selector:e.stateElementSelector},o);t=document.querySelector(i)}t&&(s?t.setAttribute(e.stateAttribute,""):t.removeAttribute(e.stateAttribute))}}function Me(t){return t.trim()}function St(t){const e=t.element;if(!e)return null;const o=e.querySelector("label");if(o&&o.textContent)return Me(o.textContent);const s=e.textContent;return s?Me(s):null}class $t{constructor(){this._localClassNameMap=new Map}get stylesheet(){return this.ensureStylesheet(),this._stylesheet}add(e){this.ensureStylesheet(),this._stylesheet.replaceSync(e)}clear(){this.ensureStylesheet(),this._stylesheet.replaceSync("")}previewLocalClassName(e,o){if(!e)return;const s=this._localClassNameMap.get(e);s&&(e.classList.remove(s),e.overlayClass=null),o?(e.classList.add(o),e.overlayClass=o,this._localClassNameMap.set(e,o)):this._localClassNameMap.delete(e)}ensureStylesheet(){this._stylesheet||(this._stylesheet=new CSSStyleSheet,this._stylesheet.replaceSync(""),document.adoptedStyleSheets=[...document.adoptedStyleSheets,this._stylesheet])}}const M=new $t;class Tt{constructor(e){this.pendingRequests={},this.requestCounter=0,this.globalUiId=this.getGlobalUiId(),this.wrappedConnection=e;const o=this.wrappedConnection.onMessage;this.wrappedConnection.onMessage=s=>{s.command==="themeEditorResponse"?this.handleResponse(s.data):o.call(this.wrappedConnection,s)}}sendRequest(e,o){const s=(this.requestCounter++).toString(),i=o.uiId??this.globalUiId;return new Promise((r,n)=>{this.wrappedConnection.send(e,{...o,requestId:s,uiId:i}),this.pendingRequests[s]={resolve:r,reject:n}})}handleResponse(e){const o=this.pendingRequests[e.requestId];if(!o){console.warn("Received response for unknown request");return}delete this.pendingRequests[e.requestId],e.code==="ok"?o.resolve(e):o.reject(e)}loadComponentMetadata(e){return this.sendRequest("themeEditorComponentMetadata",{nodeId:e.nodeId})}setLocalClassName(e,o){return this.sendRequest("themeEditorLocalClassName",{nodeId:e.nodeId,className:o})}setCssRules(e){return this.sendRequest("themeEditorRules",{rules:e})}loadRules(e){return this.sendRequest("themeEditorLoadRules",{selectors:e})}markAsUsed(){return this.sendRequest("themeEditorMarkAsUsed",{})}undo(e){return this.sendRequest("themeEditorHistory",{undo:e})}redo(e){return this.sendRequest("themeEditorHistory",{redo:e})}openCss(e){return this.sendRequest("themeEditorOpenCss",{selector:e})}getGlobalUiId(){const e=window.Vaadin;if(e&&e.Flow){const{clients:o}=e.Flow,s=Object.keys(o);for(const i of s){const r=o[i];if(r.getNodeId)return r.getUIId()}}return-1}}const x={index:-1,entries:[]};class Nt{constructor(e){this.api=e}get allowUndo(){return x.index>=0}get allowRedo(){return x.index<x.entries.length-1}get allowedActions(){return{allowUndo:this.allowUndo,allowRedo:this.allowRedo}}push(e,o,s){const i={requestId:e,execute:o,rollback:s};if(x.index++,x.entries=x.entries.slice(0,x.index),x.entries.push(i),o)try{o()}catch(r){console.error("Execute history entry failed",r)}return this.allowedActions}async undo(){if(!this.allowUndo)return this.allowedActions;const e=x.entries[x.index];x.index--;try{await this.api.undo(e.requestId),e.rollback&&e.rollback()}catch(o){console.error("Undo failed",o)}return this.allowedActions}async redo(){if(!this.allowRedo)return this.allowedActions;x.index++;const e=x.entries[x.index];try{await this.api.redo(e.requestId),e.execute&&e.execute()}catch(o){console.error("Redo failed",o)}return this.allowedActions}static clear(){x.entries=[],x.index=-1}}var It=Object.defineProperty,At=Object.getOwnPropertyDescriptor,O=(t,e,o,s)=>{for(var i=s>1?void 0:s?At(e,o):e,r=t.length-1,n;r>=0;r--)(n=t[r])&&(i=(s?n(e,o,i):n(i))||i);return s&&i&&It(e,o,i),i};class Pt extends CustomEvent{constructor(e,o,s){super("theme-property-value-change",{bubbles:!0,composed:!0,detail:{element:e,property:o,value:s}})}}class S extends ${constructor(){super(...arguments),this.value=""}static get styles(){return[et,g`
        :host {
          display: block;
        }

        .editor-row .label .modified {
          display: inline-block;
          width: 6px;
          height: 6px;
          background: orange;
          border-radius: 3px;
          margin-left: 3px;
        }
      `]}update(e){super.update(e),(e.has("propertyMetadata")||e.has("theme"))&&this.updateValueFromTheme()}render(){var e;return l`
      <div class="editor-row">
        <div class="label">
          ${this.propertyMetadata.displayName}
          ${(e=this.propertyValue)!=null&&e.modified?l`<span class="modified"></span>`:null}
        </div>
        <div class="editor">${this.renderEditor()}</div>
      </div>
    `}updateValueFromTheme(){var e;this.propertyValue=this.theme.getPropertyValue(this.elementMetadata.selector,this.propertyMetadata.propertyName),this.value=((e=this.propertyValue)==null?void 0:e.value)||""}dispatchChange(e){this.dispatchEvent(new Pt(this.elementMetadata,this.propertyMetadata,e))}}O([p({})],S.prototype,"elementMetadata",2);O([p({})],S.prototype,"propertyMetadata",2);O([p({})],S.prototype,"theme",2);O([f()],S.prototype,"propertyValue",2);O([f()],S.prototype,"value",2);class re{constructor(e){if(this._values=[],this._rawValues={},e){const o=e.propertyName,s=e.presets??[];this._values=(s||[]).map(r=>r.startsWith("--")?`var(${r})`:r);const i=document.createElement("div");i.style.borderStyle="solid",i.style.visibility="hidden",document.body.append(i);try{this._values.forEach(r=>{i.style.setProperty(o,r);const n=getComputedStyle(i);this._rawValues[r]=n.getPropertyValue(o).trim()})}finally{i.remove()}}}get values(){return this._values}get rawValues(){return this._rawValues}tryMapToRawValue(e){return this._rawValues[e]??e}tryMapToPreset(e){return this.findPreset(e)??e}findPreset(e){const o=e&&e.trim();return this.values.find(s=>this._rawValues[s]===o)}}class De extends CustomEvent{constructor(e){super("change",{detail:{value:e}})}}let ne=class extends ${constructor(){super(...arguments),this.value="",this.showClearButton=!1}static get styles(){return g`
      :host {
        display: inline-block;
        width: 100%;
        position: relative;
      }

      input {
        width: 100%;
        box-sizing: border-box;
        padding: 0.25rem 0.375rem;
        color: inherit;
        background: rgba(0, 0, 0, 0.2);
        border-radius: 0.25rem;
        border: none;
      }

      button {
        display: none;
        position: absolute;
        right: 4px;
        top: 4px;
        padding: 0;
        line-height: 0;
        border: none;
        background: none;
        color: var(--dev-tools-text-color);
      }

      button svg {
        width: 16px;
        height: 16px;
      }

      button:not(:disabled):hover {
        color: var(--dev-tools-text-color-emphasis);
      }

      :host(.show-clear-button) input {
        padding-right: 20px;
      }

      :host(.show-clear-button) button {
        display: block;
      }
    `}update(t){super.update(t),t.has("showClearButton")&&(this.showClearButton?this.classList.add("show-clear-button"):this.classList.remove("show-clear-button"))}render(){return l`
      <input class="input" .value=${this.value} @change=${this.handleInputChange} />
      <button @click=${this.handleClearClick}>${se.cross}</button>
    `}handleInputChange(t){const e=t.target;this.dispatchEvent(new De(e.value))}handleClearClick(){this.dispatchEvent(new De(""))}};O([p({})],ne.prototype,"value",2);O([p({})],ne.prototype,"showClearButton",2);ne=O([k("vaadin-dev-tools-theme-text-input")],ne);var Ot=Object.defineProperty,Rt=Object.getOwnPropertyDescriptor,ue=(t,e,o,s)=>{for(var i=s>1?void 0:s?Rt(e,o):e,r=t.length-1,n;r>=0;r--)(n=t[r])&&(i=(s?n(e,o,i):n(i))||i);return s&&i&&Ot(e,o,i),i};class Lt extends CustomEvent{constructor(e){super("class-name-change",{detail:{value:e}})}}let K=class extends ${constructor(){super(...arguments),this.editedClassName="",this.invalid=!1}static get styles(){return[et,g`
        .editor-row {
          padding-top: 0;
        }

        .editor-row .editor .error {
          display: inline-block;
          color: var(--dev-tools-red-color);
          margin-top: 4px;
        }
      `]}update(t){super.update(t),t.has("className")&&(this.editedClassName=this.className,this.invalid=!1)}render(){return l` <div class="editor-row local-class-name">
      <div class="label">CSS class name</div>
      <div class="editor">
        <vaadin-dev-tools-theme-text-input
          type="text"
          .value=${this.editedClassName}
          @change=${this.handleInputChange}
        ></vaadin-dev-tools-theme-text-input>
        ${this.invalid?l`<br /><span class="error">Please enter a valid CSS class name</span>`:null}
      </div>
    </div>`}handleInputChange(t){this.editedClassName=t.detail.value;const e=/^-?[_a-zA-Z]+[_a-zA-Z0-9-]*$/;this.invalid=!this.editedClassName.match(e),!this.invalid&&this.editedClassName!==this.className&&this.dispatchEvent(new Lt(this.editedClassName))}};ue([p({})],K.prototype,"className",2);ue([f()],K.prototype,"editedClassName",2);ue([f()],K.prototype,"invalid",2);K=ue([k("vaadin-dev-tools-theme-class-name-editor")],K);var Vt=Object.defineProperty,Mt=Object.getOwnPropertyDescriptor,me=(t,e,o,s)=>{for(var i=s>1?void 0:s?Mt(e,o):e,r=t.length-1,n;r>=0;r--)(n=t[r])&&(i=(s?n(e,o,i):n(i))||i);return s&&i&&Vt(e,o,i),i};class Dt extends CustomEvent{constructor(e){super("scope-change",{detail:{value:e}})}}Ce(g`
  vaadin-select-overlay[theme~='vaadin-dev-tools-theme-scope-selector'] {
    --lumo-primary-color-50pct: rgba(255, 255, 255, 0.5);
    z-index: 100000 !important;
  }

  vaadin-select-overlay[theme~='vaadin-dev-tools-theme-scope-selector']::part(overlay) {
    background: #333;
  }

  vaadin-select-overlay[theme~='vaadin-dev-tools-theme-scope-selector'] vaadin-item {
    color: rgba(255, 255, 255, 0.8);
  }

  vaadin-select-overlay[theme~='vaadin-dev-tools-theme-scope-selector'] vaadin-item::part(content) {
    font-size: 13px;
  }

  vaadin-select-overlay[theme~='vaadin-dev-tools-theme-scope-selector'] vaadin-item .title {
    color: rgba(255, 255, 255, 0.95);
    font-weight: bold;
  }

  vaadin-select-overlay[theme~='vaadin-dev-tools-theme-scope-selector'] vaadin-item::part(checkmark) {
    margin: 6px;
  }

  vaadin-select-overlay[theme~='vaadin-dev-tools-theme-scope-selector'] vaadin-item::part(checkmark)::before {
    color: rgba(255, 255, 255, 0.95);
  }

  vaadin-select-overlay[theme~='vaadin-dev-tools-theme-scope-selector'] vaadin-item:hover {
    background: rgba(255, 255, 255, 0.1);
  }
`);let Y=class extends ${constructor(){super(...arguments),this.value=E.local}static get styles(){return g`
      vaadin-select {
        --lumo-primary-color-50pct: rgba(255, 255, 255, 0.5);
        width: 100px;
      }

      vaadin-select::part(input-field) {
        background: rgba(0, 0, 0, 0.2);
      }

      vaadin-select vaadin-select-value-button,
      vaadin-select::part(toggle-button) {
        color: var(--dev-tools-text-color);
      }

      vaadin-select:hover vaadin-select-value-button,
      vaadin-select:hover::part(toggle-button) {
        color: var(--dev-tools-text-color-emphasis);
      }

      vaadin-select vaadin-select-item {
        font-size: 13px;
      }
    `}update(t){var e;super.update(t),t.has("metadata")&&((e=this.select)==null||e.requestContentUpdate())}render(){return l` <vaadin-select
      theme="small vaadin-dev-tools-theme-scope-selector"
      .value=${this.value}
      .renderer=${this.selectRenderer.bind(this)}
      @value-changed=${this.handleValueChange}
    ></vaadin-select>`}selectRenderer(t){var s;const e=((s=this.metadata)==null?void 0:s.displayName)||"Component",o=`${e}s`;Xe(l`
        <vaadin-list-box>
          <vaadin-item value=${E.local} label="Local">
            <span class="title">Local</span>
            <br />
            <span>Edit styles for this ${e}</span>
          </vaadin-item>
          <vaadin-item value=${E.global} label="Global">
            <span class="title">Global</span>
            <br />
            <span>Edit styles for all ${o}</span>
          </vaadin-item>
        </vaadin-list-box>
      `,t)}handleValueChange(t){const e=t.detail.value;e!==this.value&&this.dispatchEvent(new Dt(e))}};me([p({})],Y.prototype,"value",2);me([p({})],Y.prototype,"metadata",2);me([Z("vaadin-select")],Y.prototype,"select",2);Y=me([k("vaadin-dev-tools-theme-scope-selector")],Y);var zt=Object.defineProperty,Ut=Object.getOwnPropertyDescriptor,Bt=(t,e,o,s)=>{for(var i=s>1?void 0:s?Ut(e,o):e,r=t.length-1,n;r>=0;r--)(n=t[r])&&(i=(s?n(e,o,i):n(i))||i);return s&&i&&zt(e,o,i),i};let ze=class extends S{static get styles(){return[S.styles,g`
        .editor-row {
          align-items: center;
        }
      `]}handleInputChange(t){const o=t.target.checked?this.propertyMetadata.checkedValue:"";this.dispatchChange(o||"")}renderEditor(){const t=this.value===this.propertyMetadata.checkedValue;return l` <input type="checkbox" .checked=${t} @change=${this.handleInputChange} /> `}};ze=Bt([k("vaadin-dev-tools-theme-checkbox-property-editor")],ze);var Ht=Object.defineProperty,qt=Object.getOwnPropertyDescriptor,jt=(t,e,o,s)=>{for(var i=s>1?void 0:s?qt(e,o):e,r=t.length-1,n;r>=0;r--)(n=t[r])&&(i=(s?n(e,o,i):n(i))||i);return s&&i&&Ht(e,o,i),i};let Ue=class extends S{handleInputChange(t){this.dispatchChange(t.detail.value)}renderEditor(){var t;return l`
      <vaadin-dev-tools-theme-text-input
        .value=${this.value}
        .showClearButton=${((t=this.propertyValue)==null?void 0:t.modified)||!1}
        @change=${this.handleInputChange}
      ></vaadin-dev-tools-theme-text-input>
    `}};Ue=jt([k("vaadin-dev-tools-theme-text-property-editor")],Ue);var Ft=Object.defineProperty,Gt=Object.getOwnPropertyDescriptor,Ee=(t,e,o,s)=>{for(var i=s>1?void 0:s?Gt(e,o):e,r=t.length-1,n;r>=0;r--)(n=t[r])&&(i=(s?n(e,o,i):n(i))||i);return s&&i&&Ft(e,o,i),i};let ae=class extends S{constructor(){super(...arguments),this.selectedPresetIndex=-1,this.presets=new re}static get styles(){return[S.styles,g`
        :host {
          --preset-count: 3;
          --slider-bg: #fff;
          --slider-border: #333;
        }

        .editor-row {
          align-items: center;
        }

        .editor-row > .editor {
          display: flex;
          align-items: center;
          gap: 1rem;
        }

        .editor-row .input {
          flex: 0 0 auto;
          width: 80px;
        }

        .slider-wrapper {
          flex: 1 1 0;
          display: flex;
          align-items: center;
          gap: 0.5rem;
        }

        .icon {
          width: 20px;
          height: 20px;
          color: #aaa;
        }

        .icon.prefix > svg {
          transform: scale(0.75);
        }

        .slider {
          flex: 1 1 0;
          -webkit-appearance: none;
          background: linear-gradient(to right, #666, #666 2px, transparent 2px);
          background-size: calc((100% - 13px) / (var(--preset-count) - 1)) 8px;
          background-position: 5px 50%;
          background-repeat: repeat-x;
        }

        .slider::-webkit-slider-runnable-track {
          width: 100%;
          box-sizing: border-box;
          height: 16px;
          background-image: linear-gradient(#666, #666);
          background-size: calc(100% - 12px) 2px;
          background-repeat: no-repeat;
          background-position: 6px 50%;
        }

        .slider::-moz-range-track {
          width: 100%;
          box-sizing: border-box;
          height: 16px;
          background-image: linear-gradient(#666, #666);
          background-size: calc(100% - 12px) 2px;
          background-repeat: no-repeat;
          background-position: 6px 50%;
        }

        .slider::-webkit-slider-thumb {
          -webkit-appearance: none;
          height: 16px;
          width: 16px;
          border: 2px solid var(--slider-border);
          border-radius: 50%;
          background: var(--slider-bg);
          cursor: pointer;
        }

        .slider::-moz-range-thumb {
          height: 16px;
          width: 16px;
          border: 2px solid var(--slider-border);
          border-radius: 50%;
          background: var(--slider-bg);
          cursor: pointer;
        }

        .custom-value {
          opacity: 0.5;
        }

        .custom-value:hover,
        .custom-value:focus-within {
          opacity: 1;
        }

        .custom-value:not(:hover, :focus-within) {
          --slider-bg: #333;
          --slider-border: #666;
        }
      `]}update(t){t.has("propertyMetadata")&&(this.presets=new re(this.propertyMetadata)),super.update(t)}renderEditor(){var o;const t={"slider-wrapper":!0,"custom-value":this.selectedPresetIndex<0},e=this.presets.values.length;return l`
      <div class=${Ze(t)}>
        ${null}
        <input
          type="range"
          class="slider"
          style="--preset-count: ${e}"
          step="1"
          min="0"
          .max=${(e-1).toString()}
          .value=${this.selectedPresetIndex}
          @input=${this.handleSliderInput}
          @change=${this.handleSliderChange}
        />
        ${null}
      </div>
      <vaadin-dev-tools-theme-text-input
        class="input"
        .value=${this.value}
        .showClearButton=${((o=this.propertyValue)==null?void 0:o.modified)||!1}
        @change=${this.handleValueChange}
      ></vaadin-dev-tools-theme-text-input>
    `}handleSliderInput(t){const e=t.target,o=parseInt(e.value),s=this.presets.values[o];this.selectedPresetIndex=o,this.value=this.presets.rawValues[s]}handleSliderChange(){this.dispatchChange(this.value)}handleValueChange(t){this.value=t.detail.value,this.updateSliderValue(),this.dispatchChange(this.value)}dispatchChange(t){const e=this.presets.tryMapToPreset(t);super.dispatchChange(e)}updateValueFromTheme(){var t;super.updateValueFromTheme(),this.value=this.presets.tryMapToRawValue(((t=this.propertyValue)==null?void 0:t.value)||""),this.updateSliderValue()}updateSliderValue(){const t=this.presets.findPreset(this.value);this.selectedPresetIndex=t?this.presets.values.indexOf(t):-1}};Ee([f()],ae.prototype,"selectedPresetIndex",2);Ee([f()],ae.prototype,"presets",2);ae=Ee([k("vaadin-dev-tools-theme-range-property-editor")],ae);const q=(t,e=0,o=1)=>t>o?o:t<e?e:t,C=(t,e=0,o=Math.pow(10,e))=>Math.round(o*t)/o,tt=({h:t,s:e,v:o,a:s})=>{const i=(200-e)*o/100;return{h:C(t),s:C(i>0&&i<200?e*o/100/(i<=100?i:200-i)*100:0),l:C(i/2),a:C(s,2)}},xe=t=>{const{h:e,s:o,l:s}=tt(t);return`hsl(${e}, ${o}%, ${s}%)`},ge=t=>{const{h:e,s:o,l:s,a:i}=tt(t);return`hsla(${e}, ${o}%, ${s}%, ${i})`},Wt=({h:t,s:e,v:o,a:s})=>{t=t/360*6,e=e/100,o=o/100;const i=Math.floor(t),r=o*(1-e),n=o*(1-(t-i)*e),a=o*(1-(1-t+i)*e),d=i%6;return{r:C([o,n,r,r,a,o][d]*255),g:C([a,o,o,n,r,r][d]*255),b:C([r,r,a,o,o,n][d]*255),a:C(s,2)}},Kt=t=>{const{r:e,g:o,b:s,a:i}=Wt(t);return`rgba(${e}, ${o}, ${s}, ${i})`},Yt=t=>{const o=/rgba?\(?\s*(-?\d*\.?\d+)(%)?[,\s]+(-?\d*\.?\d+)(%)?[,\s]+(-?\d*\.?\d+)(%)?,?\s*[/\s]*(-?\d*\.?\d+)?(%)?\s*\)?/i.exec(t);return o?Jt({r:Number(o[1])/(o[2]?100/255:1),g:Number(o[3])/(o[4]?100/255:1),b:Number(o[5])/(o[6]?100/255:1),a:o[7]===void 0?1:Number(o[7])/(o[8]?100:1)}):{h:0,s:0,v:0,a:1}},Jt=({r:t,g:e,b:o,a:s})=>{const i=Math.max(t,e,o),r=i-Math.min(t,e,o),n=r?i===t?(e-o)/r:i===e?2+(o-t)/r:4+(t-e)/r:0;return{h:C(60*(n<0?n+6:n)),s:C(i?r/i*100:0),v:C(i/255*100),a:s}},Xt=(t,e)=>{if(t===e)return!0;for(const o in t)if(t[o]!==e[o])return!1;return!0},Zt=(t,e)=>t.replace(/\s/g,"")===e.replace(/\s/g,""),Be={},ot=t=>{let e=Be[t];return e||(e=document.createElement("template"),e.innerHTML=t,Be[t]=e),e},ke=(t,e,o)=>{t.dispatchEvent(new CustomEvent(e,{bubbles:!0,detail:o}))};let B=!1;const _e=t=>"touches"in t,Qt=t=>B&&!_e(t)?!1:(B||(B=_e(t)),!0),He=(t,e)=>{const o=_e(e)?e.touches[0]:e,s=t.el.getBoundingClientRect();ke(t.el,"move",t.getMove({x:q((o.pageX-(s.left+window.pageXOffset))/s.width),y:q((o.pageY-(s.top+window.pageYOffset))/s.height)}))},eo=(t,e)=>{const o=e.keyCode;o>40||t.xy&&o<37||o<33||(e.preventDefault(),ke(t.el,"move",t.getMove({x:o===39?.01:o===37?-.01:o===34?.05:o===33?-.05:o===35?1:o===36?-1:0,y:o===40?.01:o===38?-.01:0},!0)))};class Se{constructor(e,o,s,i){const r=ot(`<div role="slider" tabindex="0" part="${o}" ${s}><div part="${o}-pointer"></div></div>`);e.appendChild(r.content.cloneNode(!0));const n=e.querySelector(`[part=${o}]`);n.addEventListener("mousedown",this),n.addEventListener("touchstart",this),n.addEventListener("keydown",this),this.el=n,this.xy=i,this.nodes=[n.firstChild,n]}set dragging(e){const o=e?document.addEventListener:document.removeEventListener;o(B?"touchmove":"mousemove",this),o(B?"touchend":"mouseup",this)}handleEvent(e){switch(e.type){case"mousedown":case"touchstart":if(e.preventDefault(),!Qt(e)||!B&&e.button!=0)return;this.el.focus(),He(this,e),this.dragging=!0;break;case"mousemove":case"touchmove":e.preventDefault(),He(this,e);break;case"mouseup":case"touchend":this.dragging=!1;break;case"keydown":eo(this,e);break}}style(e){e.forEach((o,s)=>{for(const i in o)this.nodes[s].style.setProperty(i,o[i])})}}class to extends Se{constructor(e){super(e,"hue",'aria-label="Hue" aria-valuemin="0" aria-valuemax="360"',!1)}update({h:e}){this.h=e,this.style([{left:`${e/360*100}%`,color:xe({h:e,s:100,v:100,a:1})}]),this.el.setAttribute("aria-valuenow",`${C(e)}`)}getMove(e,o){return{h:o?q(this.h+e.x*360,0,360):360*e.x}}}class oo extends Se{constructor(e){super(e,"saturation",'aria-label="Color"',!0)}update(e){this.hsva=e,this.style([{top:`${100-e.v}%`,left:`${e.s}%`,color:xe(e)},{"background-color":xe({h:e.h,s:100,v:100,a:1})}]),this.el.setAttribute("aria-valuetext",`Saturation ${C(e.s)}%, Brightness ${C(e.v)}%`)}getMove(e,o){return{s:o?q(this.hsva.s+e.x*100,0,100):e.x*100,v:o?q(this.hsva.v-e.y*100,0,100):Math.round(100-e.y*100)}}}const so=':host{display:flex;flex-direction:column;position:relative;width:200px;height:200px;user-select:none;-webkit-user-select:none;cursor:default}:host([hidden]){display:none!important}[role=slider]{position:relative;touch-action:none;user-select:none;-webkit-user-select:none;outline:0}[role=slider]:last-child{border-radius:0 0 8px 8px}[part$=pointer]{position:absolute;z-index:1;box-sizing:border-box;width:28px;height:28px;display:flex;place-content:center center;transform:translate(-50%,-50%);background-color:#fff;border:2px solid #fff;border-radius:50%;box-shadow:0 2px 4px rgba(0,0,0,.2)}[part$=pointer]::after{content:"";width:100%;height:100%;border-radius:inherit;background-color:currentColor}[role=slider]:focus [part$=pointer]{transform:translate(-50%,-50%) scale(1.1)}',io="[part=hue]{flex:0 0 24px;background:linear-gradient(to right,red 0,#ff0 17%,#0f0 33%,#0ff 50%,#00f 67%,#f0f 83%,red 100%)}[part=hue-pointer]{top:50%;z-index:2}",ro="[part=saturation]{flex-grow:1;border-color:transparent;border-bottom:12px solid #000;border-radius:8px 8px 0 0;background-image:linear-gradient(to top,#000,transparent),linear-gradient(to right,#fff,rgba(255,255,255,0));box-shadow:inset 0 0 0 1px rgba(0,0,0,.05)}[part=saturation-pointer]{z-index:3}",oe=Symbol("same"),fe=Symbol("color"),qe=Symbol("hsva"),be=Symbol("update"),je=Symbol("parts"),le=Symbol("css"),de=Symbol("sliders");let no=class extends HTMLElement{static get observedAttributes(){return["color"]}get[le](){return[so,io,ro]}get[de](){return[oo,to]}get color(){return this[fe]}set color(e){if(!this[oe](e)){const o=this.colorModel.toHsva(e);this[be](o),this[fe]=e}}constructor(){super();const e=ot(`<style>${this[le].join("")}</style>`),o=this.attachShadow({mode:"open"});o.appendChild(e.content.cloneNode(!0)),o.addEventListener("move",this),this[je]=this[de].map(s=>new s(o))}connectedCallback(){if(this.hasOwnProperty("color")){const e=this.color;delete this.color,this.color=e}else this.color||(this.color=this.colorModel.defaultColor)}attributeChangedCallback(e,o,s){const i=this.colorModel.fromAttr(s);this[oe](i)||(this.color=i)}handleEvent(e){const o=this[qe],s={...o,...e.detail};this[be](s);let i;!Xt(s,o)&&!this[oe](i=this.colorModel.fromHsva(s))&&(this[fe]=i,ke(this,"color-changed",{value:i}))}[oe](e){return this.color&&this.colorModel.equal(e,this.color)}[be](e){this[qe]=e,this[je].forEach(o=>o.update(e))}};class ao extends Se{constructor(e){super(e,"alpha",'aria-label="Alpha" aria-valuemin="0" aria-valuemax="1"',!1)}update(e){this.hsva=e;const o=ge({...e,a:0}),s=ge({...e,a:1}),i=e.a*100;this.style([{left:`${i}%`,color:ge(e)},{"--gradient":`linear-gradient(90deg, ${o}, ${s}`}]);const r=C(i);this.el.setAttribute("aria-valuenow",`${r}`),this.el.setAttribute("aria-valuetext",`${r}%`)}getMove(e,o){return{a:o?q(this.hsva.a+e.x):e.x}}}const lo=`[part=alpha]{flex:0 0 24px}[part=alpha]::after{display:block;content:"";position:absolute;top:0;left:0;right:0;bottom:0;border-radius:inherit;background-image:var(--gradient);box-shadow:inset 0 0 0 1px rgba(0,0,0,.05)}[part^=alpha]{background-color:#fff;background-image:url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill-opacity=".05"><rect x="8" width="8" height="8"/><rect y="8" width="8" height="8"/></svg>')}[part=alpha-pointer]{top:50%}`;class co extends no{get[le](){return[...super[le],lo]}get[de](){return[...super[de],ao]}}const ho={defaultColor:"rgba(0, 0, 0, 1)",toHsva:Yt,fromHsva:Kt,equal:Zt,fromAttr:t=>t};class po extends co{get colorModel(){return ho}}/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */function uo(t){const e=[];for(;t;){if(t.nodeType===Node.DOCUMENT_NODE){e.push(t);break}if(t.nodeType===Node.DOCUMENT_FRAGMENT_NODE){e.push(t),t=t.host;continue}if(t.assignedSlot){t=t.assignedSlot;continue}t=t.parentNode}return e}const ye={start:"top",end:"bottom"},we={start:"left",end:"right"},Fe=new ResizeObserver(t=>{setTimeout(()=>{t.forEach(e=>{e.target.__overlay&&e.target.__overlay._updatePosition()})})}),mo=t=>class extends t{static get properties(){return{positionTarget:{type:Object,value:null},horizontalAlign:{type:String,value:"start"},verticalAlign:{type:String,value:"top"},noHorizontalOverlap:{type:Boolean,value:!1},noVerticalOverlap:{type:Boolean,value:!1},requiredVerticalSpace:{type:Number,value:0}}}static get observers(){return["__positionSettingsChanged(horizontalAlign, verticalAlign, noHorizontalOverlap, noVerticalOverlap, requiredVerticalSpace)","__overlayOpenedChanged(opened, positionTarget)"]}constructor(){super(),this.__onScroll=this.__onScroll.bind(this),this._updatePosition=this._updatePosition.bind(this)}connectedCallback(){super.connectedCallback(),this.opened&&this.__addUpdatePositionEventListeners()}disconnectedCallback(){super.disconnectedCallback(),this.__removeUpdatePositionEventListeners()}__addUpdatePositionEventListeners(){window.addEventListener("resize",this._updatePosition),this.__positionTargetAncestorRootNodes=uo(this.positionTarget),this.__positionTargetAncestorRootNodes.forEach(o=>{o.addEventListener("scroll",this.__onScroll,!0)})}__removeUpdatePositionEventListeners(){window.removeEventListener("resize",this._updatePosition),this.__positionTargetAncestorRootNodes&&(this.__positionTargetAncestorRootNodes.forEach(o=>{o.removeEventListener("scroll",this.__onScroll,!0)}),this.__positionTargetAncestorRootNodes=null)}__overlayOpenedChanged(o,s){if(this.__removeUpdatePositionEventListeners(),s&&(s.__overlay=null,Fe.unobserve(s),o&&(this.__addUpdatePositionEventListeners(),s.__overlay=this,Fe.observe(s))),o){const i=getComputedStyle(this);this.__margins||(this.__margins={},["top","bottom","left","right"].forEach(r=>{this.__margins[r]=parseInt(i[r],10)})),this.setAttribute("dir",i.direction),this._updatePosition(),requestAnimationFrame(()=>this._updatePosition())}}__positionSettingsChanged(){this._updatePosition()}__onScroll(o){this.contains(o.target)||this._updatePosition()}_updatePosition(){if(!this.positionTarget||!this.opened)return;const o=this.positionTarget.getBoundingClientRect(),s=this.__shouldAlignStartVertically(o);this.style.justifyContent=s?"flex-start":"flex-end";const i=this.__isRTL,r=this.__shouldAlignStartHorizontally(o,i),n=!i&&r||i&&!r;this.style.alignItems=n?"flex-start":"flex-end";const a=this.getBoundingClientRect(),d=this.__calculatePositionInOneDimension(o,a,this.noVerticalOverlap,ye,this,s),c=this.__calculatePositionInOneDimension(o,a,this.noHorizontalOverlap,we,this,r);Object.assign(this.style,d,c),this.toggleAttribute("bottom-aligned",!s),this.toggleAttribute("top-aligned",s),this.toggleAttribute("end-aligned",!n),this.toggleAttribute("start-aligned",n)}__shouldAlignStartHorizontally(o,s){const i=Math.max(this.__oldContentWidth||0,this.$.overlay.offsetWidth);this.__oldContentWidth=this.$.overlay.offsetWidth;const r=Math.min(window.innerWidth,document.documentElement.clientWidth),n=!s&&this.horizontalAlign==="start"||s&&this.horizontalAlign==="end";return this.__shouldAlignStart(o,i,r,this.__margins,n,this.noHorizontalOverlap,we)}__shouldAlignStartVertically(o){const s=this.requiredVerticalSpace||Math.max(this.__oldContentHeight||0,this.$.overlay.offsetHeight);this.__oldContentHeight=this.$.overlay.offsetHeight;const i=Math.min(window.innerHeight,document.documentElement.clientHeight),r=this.verticalAlign==="top";return this.__shouldAlignStart(o,s,i,this.__margins,r,this.noVerticalOverlap,ye)}__shouldAlignStart(o,s,i,r,n,a,d){const c=i-o[a?d.end:d.start]-r[d.end],m=o[a?d.start:d.end]-r[d.start],w=n?c:m,V=w>(n?m:c)||w>s;return n===V}__adjustBottomProperty(o,s,i){let r;if(o===s.end){if(s.end===ye.end){const n=Math.min(window.innerHeight,document.documentElement.clientHeight);if(i>n&&this.__oldViewportHeight){const a=this.__oldViewportHeight-n;r=i-a}this.__oldViewportHeight=n}if(s.end===we.end){const n=Math.min(window.innerWidth,document.documentElement.clientWidth);if(i>n&&this.__oldViewportWidth){const a=this.__oldViewportWidth-n;r=i-a}this.__oldViewportWidth=n}}return r}__calculatePositionInOneDimension(o,s,i,r,n,a){const d=a?r.start:r.end,c=a?r.end:r.start,m=parseFloat(n.style[d]||getComputedStyle(n)[d]),w=this.__adjustBottomProperty(d,r,m),L=s[a?r.start:r.end]-o[i===a?r.end:r.start],V=w?`${w}px`:`${m+L*(a?-1:1)}px`;return{[d]:V,[c]:""}}};var vo=Object.defineProperty,go=Object.getOwnPropertyDescriptor,D=(t,e,o,s)=>{for(var i=s>1?void 0:s?go(e,o):e,r=t.length-1,n;r>=0;r--)(n=t[r])&&(i=(s?n(e,o,i):n(i))||i);return s&&i&&vo(e,o,i),i};class fo extends CustomEvent{constructor(e){super("color-picker-change",{detail:{value:e}})}}const st=g`
  :host {
    --preview-size: 24px;
    --preview-color: rgba(0, 0, 0, 0);
  }

  .preview {
    --preview-bg-size: calc(var(--preview-size) / 2);
    --preview-bg-pos: calc(var(--preview-size) / 4);

    width: var(--preview-size);
    height: var(--preview-size);
    padding: 0;
    position: relative;
    overflow: hidden;
    background: none;
    border: solid 2px #888;
    border-radius: 4px;
    box-sizing: content-box;
  }

  .preview::before,
  .preview::after {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
  }

  .preview::before {
    content: '';
    background: white;
    background-image: linear-gradient(45deg, #666 25%, transparent 25%),
      linear-gradient(45deg, transparent 75%, #666 75%), linear-gradient(45deg, transparent 75%, #666 75%),
      linear-gradient(45deg, #666 25%, transparent 25%);
    background-size: var(--preview-bg-size) var(--preview-bg-size);
    background-position: 0 0, 0 0, calc(var(--preview-bg-pos) * -1) calc(var(--preview-bg-pos) * -1),
      var(--preview-bg-pos) var(--preview-bg-pos);
  }

  .preview::after {
    content: '';
    background-color: var(--preview-color);
  }
`;let J=class extends ${constructor(){super(...arguments),this.commitValue=!1}static get styles(){return[st,g`
        #toggle {
          display: block;
        }
      `]}update(t){super.update(t),t.has("value")&&this.overlay&&this.overlay.requestContentUpdate()}firstUpdated(){this.overlay=document.createElement("vaadin-dev-tools-color-picker-overlay"),this.overlay.renderer=this.renderOverlayContent.bind(this),this.overlay.owner=this,this.overlay.positionTarget=this.toggle,this.overlay.noVerticalOverlap=!0,this.overlay.addEventListener("vaadin-overlay-escape-press",this.handleOverlayEscape.bind(this)),this.overlay.addEventListener("vaadin-overlay-close",this.handleOverlayClose.bind(this)),this.append(this.overlay)}render(){const t=this.value||"rgba(0, 0, 0, 0)";return l` <button
      id="toggle"
      class="preview"
      style="--preview-color: ${t}"
      @click=${this.open}
    ></button>`}open(){this.commitValue=!1,this.overlay.opened=!0,this.overlay.style.zIndex="1000000";const t=this.overlay.shadowRoot.querySelector('[part="overlay"]');t.style.background="#333"}renderOverlayContent(t){const o=getComputedStyle(this.toggle,"::after").getPropertyValue("background-color");Xe(l` <div>
        <vaadin-dev-tools-color-picker-overlay-content
          .value=${o}
          .presets=${this.presets}
          @color-changed=${this.handleColorChange.bind(this)}
        ></vaadin-dev-tools-color-picker-overlay-content>
      </div>`,t)}handleColorChange(t){this.commitValue=!0,this.dispatchEvent(new fo(t.detail.value)),t.detail.close&&(this.overlay.opened=!1,this.handleOverlayClose())}handleOverlayEscape(){this.commitValue=!1}handleOverlayClose(){const t=this.commitValue?"color-picker-commit":"color-picker-cancel";this.dispatchEvent(new CustomEvent(t))}};D([p({})],J.prototype,"value",2);D([p({})],J.prototype,"presets",2);D([Z("#toggle")],J.prototype,"toggle",2);J=D([k("vaadin-dev-tools-color-picker")],J);let ce=class extends ${static get styles(){return[st,g`
        :host {
          display: block;
          padding: 12px;
        }

        .picker::part(saturation),
        .picker::part(hue) {
          margin-bottom: 10px;
        }

        .picker::part(hue),
        .picker::part(alpha) {
          flex: 0 0 20px;
        }

        .picker::part(saturation),
        .picker::part(hue),
        .picker::part(alpha) {
          border-radius: 3px;
        }

        .picker::part(saturation-pointer),
        .picker::part(hue-pointer),
        .picker::part(alpha-pointer) {
          width: 20px;
          height: 20px;
        }

        .swatches {
          display: grid;
          grid-template-columns: repeat(6, var(--preview-size));
          grid-column-gap: 10px;
          grid-row-gap: 6px;
          margin-top: 16px;
        }
      `]}render(){return l` <div>
      <vaadin-dev-tools-rgba-string-color-picker
        class="picker"
        .color=${this.value}
        @color-changed=${this.handlePickerChange}
      ></vaadin-dev-tools-rgba-string-color-picker>
      ${this.renderSwatches()}
    </div>`}renderSwatches(){if(!this.presets||this.presets.length===0)return;const t=this.presets.map(e=>l` <button
        class="preview"
        style="--preview-color: ${e}"
        @click=${()=>this.selectPreset(e)}
      ></button>`);return l` <div class="swatches">${t}</div>`}handlePickerChange(t){this.dispatchEvent(new CustomEvent("color-changed",{detail:{value:t.detail.value}}))}selectPreset(t){this.dispatchEvent(new CustomEvent("color-changed",{detail:{value:t,close:!0}}))}};D([p({})],ce.prototype,"value",2);D([p({})],ce.prototype,"presets",2);ce=D([k("vaadin-dev-tools-color-picker-overlay-content")],ce);customElements.whenDefined("vaadin-overlay").then(()=>{const t=customElements.get("vaadin-overlay");class e extends mo(t){}customElements.define("vaadin-dev-tools-color-picker-overlay",e)});customElements.define("vaadin-dev-tools-rgba-string-color-picker",po);var bo=Object.defineProperty,yo=Object.getOwnPropertyDescriptor,wo=(t,e,o,s)=>{for(var i=s>1?void 0:s?yo(e,o):e,r=t.length-1,n;r>=0;r--)(n=t[r])&&(i=(s?n(e,o,i):n(i))||i);return s&&i&&bo(e,o,i),i};let Ge=class extends S{constructor(){super(...arguments),this.presets=new re}static get styles(){return[S.styles,g`
        .editor-row {
          align-items: center;
        }

        .editor-row > .editor {
          display: flex;
          align-items: center;
          gap: 0.5rem;
        }
      `]}update(t){t.has("propertyMetadata")&&(this.presets=new re(this.propertyMetadata)),super.update(t)}renderEditor(){var t;return l`
      <vaadin-dev-tools-color-picker
        .value=${this.value}
        .presets=${this.presets.values}
        @color-picker-change=${this.handleColorPickerChange}
        @color-picker-commit=${this.handleColorPickerCommit}
        @color-picker-cancel=${this.handleColorPickerCancel}
      ></vaadin-dev-tools-color-picker>
      <vaadin-dev-tools-theme-text-input
        .value=${this.value}
        .showClearButton=${((t=this.propertyValue)==null?void 0:t.modified)||!1}
        @change=${this.handleInputChange}
      ></vaadin-dev-tools-theme-text-input>
    `}handleInputChange(t){this.value=t.detail.value,this.dispatchChange(this.value)}handleColorPickerChange(t){this.value=t.detail.value}handleColorPickerCommit(){this.dispatchChange(this.value)}handleColorPickerCancel(){this.updateValueFromTheme()}dispatchChange(t){const e=this.presets.tryMapToPreset(t);super.dispatchChange(e)}updateValueFromTheme(){var t;super.updateValueFromTheme(),this.value=this.presets.tryMapToRawValue(((t=this.propertyValue)==null?void 0:t.value)||"")}};Ge=wo([k("vaadin-dev-tools-theme-color-property-editor")],Ge);var xo=Object.defineProperty,_o=Object.getOwnPropertyDescriptor,$e=(t,e,o,s)=>{for(var i=s>1?void 0:s?_o(e,o):e,r=t.length-1,n;r>=0;r--)(n=t[r])&&(i=(s?n(e,o,i):n(i))||i);return s&&i&&xo(e,o,i),i};class Co extends CustomEvent{constructor(e){super("open-css",{detail:{element:e}})}}let he=class extends ${static get styles(){return g`
      .section .header {
        display: flex;
        align-items: baseline;
        justify-content: space-between;
        padding: 0.4rem var(--theme-editor-section-horizontal-padding);
        color: var(--dev-tools-text-color-emphasis);
        background-color: rgba(0, 0, 0, 0.2);
      }

      .section .property-list .property-editor:not(:last-child) {
        border-bottom: solid 1px rgba(0, 0, 0, 0.2);
      }

      .section .header .open-css {
        all: initial;
        font-family: inherit;
        font-size: var(--dev-tools-font-size-small);
        line-height: 1;
        white-space: nowrap;
        background-color: rgba(255, 255, 255, 0.12);
        color: var(--dev-tools-text-color);
        font-weight: 600;
        padding: 0.25rem 0.375rem;
        border-radius: 0.25rem;
      }

      .section .header .open-css:hover {
        color: var(--dev-tools-text-color-emphasis);
      }
    `}render(){const t=this.metadata.elements.map(e=>this.renderSection(e));return l` <div>${t}</div> `}renderSection(t){const e=t.properties.map(o=>this.renderPropertyEditor(t,o));return l`
      <div class="section" data-testid=${t==null?void 0:t.displayName}>
        <div class="header">
          <span> ${t.displayName} </span>
          <button class="open-css" @click=${()=>this.handleOpenCss(t)}>Edit CSS</button>
        </div>
        <div class="property-list">${e}</div>
      </div>
    `}handleOpenCss(t){this.dispatchEvent(new Co(t))}renderPropertyEditor(t,e){let o;switch(e.editorType){case y.checkbox:o=ee`vaadin-dev-tools-theme-checkbox-property-editor`;break;case y.range:o=ee`vaadin-dev-tools-theme-range-property-editor`;break;case y.color:o=ee`vaadin-dev-tools-theme-color-property-editor`;break;default:o=ee`vaadin-dev-tools-theme-text-property-editor`}return lt` <${o}
          class="property-editor"
          .elementMetadata=${t}
          .propertyMetadata=${e}
          .theme=${this.theme}
          data-testid=${e.propertyName}
        >
        </${o}>`}};$e([p({})],he.prototype,"metadata",2);$e([p({})],he.prototype,"theme",2);he=$e([k("vaadin-dev-tools-theme-property-list")],he);var Eo=Object.defineProperty,ko=Object.getOwnPropertyDescriptor,So=(t,e,o,s)=>{for(var i=s>1?void 0:s?ko(e,o):e,r=t.length-1,n;r>=0;r--)(n=t[r])&&(i=(s?n(e,o,i):n(i))||i);return s&&i&&Eo(e,o,i),i};let pe=class extends ${render(){return l`<div
      tabindex="-1"
      @mousemove=${this.onMouseMove}
      @click=${this.onClick}
      @keydown=${this.onKeyDown}
    ></div>`}onClick(t){const e=this.getTargetElement(t);this.dispatchEvent(new CustomEvent("shim-click",{detail:{target:e}}))}onMouseMove(t){const e=this.getTargetElement(t);this.dispatchEvent(new CustomEvent("shim-mousemove",{detail:{target:e}}))}onKeyDown(t){this.dispatchEvent(new CustomEvent("shim-keydown",{detail:{originalEvent:t}}))}getTargetElement(t){this.style.display="none";const e=document.elementFromPoint(t.clientX,t.clientY);return this.style.display="",e}};pe.shadowRootOptions={...$.shadowRootOptions,delegatesFocus:!0};pe.styles=[g`
      div {
        pointer-events: auto;
        background: rgba(255, 255, 255, 0);
        position: fixed;
        inset: 0px;
        z-index: 1000000;
      }
    `];pe=So([k("vaadin-dev-tools-shim")],pe);const it=g`
  .popup {
    width: auto;
    position: fixed;
    background-color: var(--dev-tools-background-color-active-blurred);
    color: var(--dev-tools-text-color-primary);
    padding: 0.1875rem 0.75rem 0.1875rem 1rem;
    background-clip: padding-box;
    border-radius: var(--dev-tools-border-radius);
    overflow: hidden;
    margin: 0.5rem;
    width: 30rem;
    max-width: calc(100% - 1rem);
    max-height: calc(100vh - 1rem);
    flex-shrink: 1;
    background-color: var(--dev-tools-background-color-active);
    color: var(--dev-tools-text-color);
    transition: var(--dev-tools-transition-duration);
    transform-origin: bottom right;
    display: flex;
    flex-direction: column;
    box-shadow: var(--dev-tools-box-shadow);
    outline: none;
  }
`;var $o=Object.defineProperty,To=Object.getOwnPropertyDescriptor,Q=(t,e,o,s)=>{for(var i=s>1?void 0:s?To(e,o):e,r=t.length-1,n;r>=0;r--)(n=t[r])&&(i=(s?n(e,o,i):n(i))||i);return s&&i&&$o(e,o,i),i};let P=class extends ${constructor(){super(...arguments),this.active=!1,this.components=[],this.selected=0}connectedCallback(){super.connectedCallback();const t=new CSSStyleSheet;t.replaceSync(`
    .vaadin-dev-tools-highlight-overlay {
      pointer-events: none;
      position: absolute;
      z-index: 10000;
      background: rgba(158,44,198,0.25);
    }`),document.adoptedStyleSheets=[...document.adoptedStyleSheets,t],this.overlayElement=document.createElement("div"),this.overlayElement.classList.add("vaadin-dev-tools-highlight-overlay")}render(){var t;return this.active?(this.style.display="block",l`
      <vaadin-dev-tools-shim
        @shim-click=${this.shimClick}
        @shim-mousemove=${this.shimMove}
        @shim-keydown=${this.shimKeydown}
      ></vaadin-dev-tools-shim>
      <div class="window popup component-picker-info">${(t=this.options)==null?void 0:t.infoTemplate}</div>
      <div class="window popup component-picker-components-info">
        <div>
          ${this.components.map((e,o)=>l`<div class=${o===this.selected?"selected":""}>
                ${e.element.tagName.toLowerCase()}
              </div>`)}
        </div>
      </div>
    `):(this.style.display="none",null)}open(t){this.options=t,this.active=!0,this.dispatchEvent(new CustomEvent("component-picker-opened",{}))}close(){this.active=!1,this.dispatchEvent(new CustomEvent("component-picker-closed",{}))}update(t){var e;if(super.update(t),(t.has("selected")||t.has("components"))&&this.highlight((e=this.components[this.selected])==null?void 0:e.element),t.has("active")){const o=t.get("active"),s=this.active;!o&&s?requestAnimationFrame(()=>this.shim.focus()):o&&!s&&this.highlight(void 0)}}shimKeydown(t){const e=t.detail.originalEvent;if(e.key==="Escape")this.close(),t.stopPropagation(),t.preventDefault();else if(e.key==="ArrowUp"){let o=this.selected-1;o<0&&(o=this.components.length-1),this.selected=o}else e.key==="ArrowDown"?this.selected=(this.selected+1)%this.components.length:e.key==="Enter"&&(this.pickSelectedComponent(),t.stopPropagation(),t.preventDefault())}shimMove(t){const e=t.detail.target;this.components=ht(e),this.selected=this.components.length-1}shimClick(t){this.pickSelectedComponent()}pickSelectedComponent(){const t=this.components[this.selected];if(t&&this.options)try{this.options.pickCallback(t)}catch(e){console.error("Pick callback failed",e)}this.close()}highlight(t){if(this.highlighted!==t)if(t){const e=t.getBoundingClientRect(),o=getComputedStyle(t);this.overlayElement.style.top=`${e.top}px`,this.overlayElement.style.left=`${e.left}px`,this.overlayElement.style.width=`${e.width}px`,this.overlayElement.style.height=`${e.height}px`,this.overlayElement.style.borderRadius=o.borderRadius,document.body.append(this.overlayElement)}else this.overlayElement.remove();this.highlighted=t}};P.styles=[it,g`
      .component-picker-info {
        left: 1em;
        bottom: 1em;
      }

      .component-picker-components-info {
        right: 3em;
        bottom: 1em;
      }

      .component-picker-components-info .selected {
        font-weight: bold;
      }
    `];Q([f()],P.prototype,"active",2);Q([f()],P.prototype,"components",2);Q([f()],P.prototype,"selected",2);Q([Z("vaadin-dev-tools-shim")],P.prototype,"shim",2);P=Q([k("vaadin-dev-tools-component-picker")],P);const No=Object.freeze(Object.defineProperty({__proto__:null,get ComponentPicker(){return P}},Symbol.toStringTag,{value:"Module"}));var Io=Object.defineProperty,Ao=Object.getOwnPropertyDescriptor,R=(t,e,o,s)=>{for(var i=s>1?void 0:s?Ao(e,o):e,r=t.length-1,n;r>=0;r--)(n=t[r])&&(i=(s?n(e,o,i):n(i))||i);return s&&i&&Io(e,o,i),i};Ce(g`
  .vaadin-theme-editor-highlight {
    outline: solid 2px #9e2cc6;
    outline-offset: 3px;
  }
`);let I=class extends ${constructor(){super(...arguments),this.expanded=!1,this.themeEditorState=W.enabled,this.context=null,this.baseTheme=null,this.editedTheme=null,this.effectiveTheme=null}static get styles(){return g`
      :host {
        animation: fade-in var(--dev-tools-transition-duration) ease-in;
        --theme-editor-section-horizontal-padding: 0.75rem;
        display: flex;
        flex-direction: column;
        max-height: 400px;
      }

      .notice {
        padding: var(--theme-editor-section-horizontal-padding);
      }

      .notice a {
        color: var(--dev-tools-text-color-emphasis);
      }

      .header {
        flex: 0 0 auto;
        border-bottom: solid 1px rgba(0, 0, 0, 0.2);
      }

      .header .picker-row {
        padding: var(--theme-editor-section-horizontal-padding);
        display: flex;
        gap: 20px;
        align-items: center;
        justify-content: space-between;
      }

      .picker {
        flex: 1 1 0;
        min-width: 0;
        display: flex;
        align-items: center;
      }

      .picker button {
        min-width: 0;
        display: inline-flex;
        align-items: center;
        padding: 0;
        line-height: 20px;
        border: none;
        background: none;
        color: var(--dev-tools-text-color);
      }

      .picker button:not(:disabled):hover {
        color: var(--dev-tools-text-color-emphasis);
      }

      .picker svg,
      .picker .component-type {
        flex: 0 0 auto;
        margin-right: 4px;
      }

      .picker .instance-name {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        color: #e5a2fce5;
      }

      .picker .instance-name-quote {
        color: #e5a2fce5;
      }

      .picker .no-selection {
        font-style: italic;
      }

      .actions {
        display: flex;
        align-items: center;
        gap: 8px;
      }

      .property-list {
        flex: 1 1 auto;
        overflow-y: auto;
      }

      .link-button {
        all: initial;
        font-family: inherit;
        font-size: var(--dev-tools-font-size-small);
        line-height: 1;
        white-space: nowrap;
        color: inherit;
        font-weight: 600;
        text-decoration: underline;
      }

      .link-button:focus,
      .link-button:hover {
        color: var(--dev-tools-text-color-emphasis);
      }

      .icon-button {
        padding: 0;
        line-height: 0;
        border: none;
        background: none;
        color: var(--dev-tools-text-color);
      }

      .icon-button:disabled {
        opacity: 0.5;
      }

      .icon-button:not(:disabled):hover {
        color: var(--dev-tools-text-color-emphasis);
      }
    `}firstUpdated(){this.api=new Tt(this.connection),this.history=new Nt(this.api),this.historyActions=this.history.allowedActions,this.api.markAsUsed(),document.addEventListener("vaadin-theme-updated",()=>{M.clear(),this.refreshTheme()})}update(t){var e,o;super.update(t),t.has("expanded")&&(this.expanded?this.highlightElement((e=this.context)==null?void 0:e.component.element):this.removeElementHighlight((o=this.context)==null?void 0:o.component.element))}disconnectedCallback(){var t;super.disconnectedCallback(),this.removeElementHighlight((t=this.context)==null?void 0:t.component.element)}render(){var t,e,o;return this.themeEditorState===W.missing_theme?this.renderMissingThemeNotice():l`
      <div class="header">
        <div class="picker-row">
          ${this.renderPicker()}
          <div class="actions">
            ${(t=this.context)!=null&&t.metadata?l` <vaadin-dev-tools-theme-scope-selector
                  .value=${this.context.scope}
                  .metadata=${this.context.metadata}
                  @scope-change=${this.handleScopeChange}
                ></vaadin-dev-tools-theme-scope-selector>`:null}
            <button
              class="icon-button"
              data-testid="undo"
              ?disabled=${!((e=this.historyActions)!=null&&e.allowUndo)}
              @click=${this.handleUndo}
            >
              ${se.undo}
            </button>
            <button
              class="icon-button"
              data-testid="redo"
              ?disabled=${!((o=this.historyActions)!=null&&o.allowRedo)}
              @click=${this.handleRedo}
            >
              ${se.redo}
            </button>
          </div>
        </div>
        ${this.renderLocalClassNameEditor()}
      </div>
      ${this.renderPropertyList()}
    `}renderMissingThemeNotice(){return l`
      <div class="notice">
        It looks like you have not set up a custom theme yet. Theme editor requires an existing theme to work with.
        Please check our
        <a href="https://vaadin.com/docs/latest/styling/custom-theme/creating-custom-theme" target="_blank"
          >documentation</a
        >
        on how to set up a custom theme.
      </div>
    `}renderPropertyList(){if(!this.context)return null;if(!this.context.metadata){const e=this.context.component.element.localName;return l`
        <div class="notice">Styling <code>&lt;${e}&gt;</code> components is not supported at the moment.</div>
      `}if(this.context.scope===E.local&&!this.context.accessible){const e=this.context.metadata.displayName;return l`
        <div class="notice">
          The selected ${e} can not be styled locally. Currently, theme editor only supports styling
          instances that are assigned to a local variable, like so:
          <pre><code>Button saveButton = new Button("Save");</code></pre>
          If you want to modify the code so that it satisfies this requirement,
          <button class="link-button" @click=${this.handleShowComponent}>click here</button>
          to open it in your IDE. Alternatively you can choose to style all ${e}s by selecting "Global" from
          the scope dropdown above.
        </div>
      `}return l` <vaadin-dev-tools-theme-property-list
      class="property-list"
      .metadata=${this.context.metadata}
      .theme=${this.effectiveTheme}
      @theme-property-value-change=${this.handlePropertyChange}
      @open-css=${this.handleOpenCss}
    ></vaadin-dev-tools-theme-property-list>`}handleShowComponent(){if(!this.context)return;const t=this.context.component,e={nodeId:t.nodeId,uiId:t.uiId};this.connection.sendShowComponentCreateLocation(e)}async handleOpenCss(t){if(!this.context)return;await this.ensureLocalClassName();const e={themeScope:this.context.scope,localClassName:this.context.localClassName},o=H(t.detail.element,e);await this.api.openCss(o)}renderPicker(){var e;let t;if((e=this.context)!=null&&e.metadata){const o=this.context.scope===E.local?this.context.metadata.displayName:`All ${this.context.metadata.displayName}s`,s=l`<span class="component-type">${o}</span>`,i=this.context.scope===E.local?St(this.context.component):null,r=i?l` <span class="instance-name-quote">"</span><span class="instance-name">${i}</span
            ><span class="instance-name-quote">"</span>`:null;t=l`${s} ${r}`}else t=l`<span class="no-selection">Pick an element to get started</span>`;return l`
      <div class="picker">
        <button @click=${this.pickComponent}>${se.crosshair} ${t}</button>
      </div>
    `}renderLocalClassNameEditor(){var o;const t=((o=this.context)==null?void 0:o.scope)===E.local&&this.context.accessible;if(!this.context||!t)return null;const e=this.context.localClassName||this.context.suggestedClassName;return l` <vaadin-dev-tools-theme-class-name-editor
      .className=${e}
      @class-name-change=${this.handleClassNameChange}
    >
    </vaadin-dev-tools-theme-class-name-editor>`}async handleClassNameChange(t){if(!this.context)return;const e=this.context.localClassName,o=t.detail.value;if(e){const s=this.context.component.element;this.context.localClassName=o;const i=await this.api.setLocalClassName(this.context.component,o);this.historyActions=this.history.push(i.requestId,()=>M.previewLocalClassName(s,o),()=>M.previewLocalClassName(s,e))}else this.context={...this.context,suggestedClassName:o}}async pickComponent(){var t;this.removeElementHighlight((t=this.context)==null?void 0:t.component.element),this.pickerProvider().open({infoTemplate:l`
        <div>
          <h3>Locate the component to style</h3>
          <p>Use the mouse cursor to highlight components in the UI.</p>
          <p>Use arrow down/up to cycle through and highlight specific components under the cursor.</p>
          <p>Click the primary mouse button to select the component.</p>
        </div>
      `,pickCallback:async e=>{var s;const o=await _t.getMetadata(e);if(!o){this.context={component:e,scope:((s=this.context)==null?void 0:s.scope)||E.local},this.baseTheme=null,this.editedTheme=null,this.effectiveTheme=null;return}this.highlightElement(e.element),this.refreshComponentAndTheme(e,o)}})}handleScopeChange(t){this.context&&this.refreshTheme({...this.context,scope:t.detail.value})}async handlePropertyChange(t){if(!this.context||!this.baseTheme||!this.editedTheme)return;const{element:e,property:o,value:s}=t.detail;this.editedTheme.updatePropertyValue(e.selector,o.propertyName,s,!0),this.effectiveTheme=A.combine(this.baseTheme,this.editedTheme),await this.ensureLocalClassName();const i={themeScope:this.context.scope,localClassName:this.context.localClassName},r=Ct(e,i,o.propertyName,s);try{const n=await this.api.setCssRules([r]);this.historyActions=this.history.push(n.requestId);const a=Et(r);M.add(a)}catch(n){console.error("Failed to update property value",n)}}async handleUndo(){this.historyActions=await this.history.undo(),await this.refreshComponentAndTheme()}async handleRedo(){this.historyActions=await this.history.redo(),await this.refreshComponentAndTheme()}async ensureLocalClassName(){if(!this.context||this.context.scope===E.global||this.context.localClassName)return;if(!this.context.localClassName&&!this.context.suggestedClassName)throw new Error("Cannot assign local class name for the component because it does not have a suggested class name");const t=this.context.component.element,e=this.context.suggestedClassName;this.context.localClassName=e;const o=await this.api.setLocalClassName(this.context.component,e);this.historyActions=this.history.push(o.requestId,()=>M.previewLocalClassName(t,e),()=>M.previewLocalClassName(t))}async refreshComponentAndTheme(t,e){var s,i,r;if(t=t||((s=this.context)==null?void 0:s.component),e=e||((i=this.context)==null?void 0:i.metadata),!t||!e)return;const o=await this.api.loadComponentMetadata(t);M.previewLocalClassName(t.element,o.className),await this.refreshTheme({scope:((r=this.context)==null?void 0:r.scope)||E.local,metadata:e,component:t,localClassName:o.className,suggestedClassName:o.suggestedClassName,accessible:o.accessible})}async refreshTheme(t){const e=t||this.context;if(!e||!e.metadata)return;if(e.scope===E.local&&!e.accessible){this.context=e,this.baseTheme=null,this.editedTheme=null,this.effectiveTheme=null;return}let s=new A(e.metadata);if(!(e.scope===E.local&&!e.localClassName)){const n={themeScope:e.scope,localClassName:e.localClassName},a=e.metadata.elements.map(c=>H(c,n)),d=await this.api.loadRules(a);s=A.fromServerRules(e.metadata,n,d.rules)}const r=await kt(e.metadata);this.context=e,this.baseTheme=r,this.editedTheme=s,this.effectiveTheme=A.combine(r,this.editedTheme)}highlightElement(t){t&&t.classList.add("vaadin-theme-editor-highlight")}removeElementHighlight(t){t&&t.classList.remove("vaadin-theme-editor-highlight")}};R([p({})],I.prototype,"expanded",2);R([p({})],I.prototype,"themeEditorState",2);R([p({})],I.prototype,"pickerProvider",2);R([p({})],I.prototype,"connection",2);R([f()],I.prototype,"historyActions",2);R([f()],I.prototype,"context",2);R([f()],I.prototype,"effectiveTheme",2);I=R([k("vaadin-dev-tools-theme-editor")],I);var Po=function(){var t=document.getSelection();if(!t.rangeCount)return function(){};for(var e=document.activeElement,o=[],s=0;s<t.rangeCount;s++)o.push(t.getRangeAt(s));switch(e.tagName.toUpperCase()){case"INPUT":case"TEXTAREA":e.blur();break;default:e=null;break}return t.removeAllRanges(),function(){t.type==="Caret"&&t.removeAllRanges(),t.rangeCount||o.forEach(function(i){t.addRange(i)}),e&&e.focus()}},We={"text/plain":"Text","text/html":"Url",default:"Text"},Oo="Copy to clipboard: #{key}, Enter";function Ro(t){var e=(/mac os x/i.test(navigator.userAgent)?"⌘":"Ctrl")+"+C";return t.replace(/#{\s*key\s*}/g,e)}function Lo(t,e){var o,s,i,r,n,a,d=!1;e||(e={}),o=e.debug||!1;try{i=Po(),r=document.createRange(),n=document.getSelection(),a=document.createElement("span"),a.textContent=t,a.style.all="unset",a.style.position="fixed",a.style.top=0,a.style.clip="rect(0, 0, 0, 0)",a.style.whiteSpace="pre",a.style.webkitUserSelect="text",a.style.MozUserSelect="text",a.style.msUserSelect="text",a.style.userSelect="text",a.addEventListener("copy",function(m){if(m.stopPropagation(),e.format)if(m.preventDefault(),typeof m.clipboardData>"u"){o&&console.warn("unable to use e.clipboardData"),o&&console.warn("trying IE specific stuff"),window.clipboardData.clearData();var w=We[e.format]||We.default;window.clipboardData.setData(w,t)}else m.clipboardData.clearData(),m.clipboardData.setData(e.format,t);e.onCopy&&(m.preventDefault(),e.onCopy(m.clipboardData))}),document.body.appendChild(a),r.selectNodeContents(a),n.addRange(r);var c=document.execCommand("copy");if(!c)throw new Error("copy command was unsuccessful");d=!0}catch(m){o&&console.error("unable to copy using execCommand: ",m),o&&console.warn("trying IE specific stuff");try{window.clipboardData.setData(e.format||"text",t),e.onCopy&&e.onCopy(window.clipboardData),d=!0}catch(w){o&&console.error("unable to copy using clipboardData: ",w),o&&console.error("falling back to prompt"),s=Ro("message"in e?e.message:Oo),window.prompt(s,t)}}finally{n&&(typeof n.removeRange=="function"?n.removeRange(r):n.removeAllRanges()),a&&document.body.removeChild(a),i()}return d}const Te=1e3,Ne=(t,e)=>{const o=Array.from(t.querySelectorAll(e.join(", "))),s=Array.from(t.querySelectorAll("*")).filter(i=>i.shadowRoot).flatMap(i=>Ne(i.shadowRoot,e));return[...o,...s]};let Ke=!1;const X=(t,e)=>{Ke||(window.addEventListener("message",i=>{i.data==="validate-license"&&window.location.reload()},!1),Ke=!0);const o=t._overlayElement;if(o){if(o.shadowRoot){const i=o.shadowRoot.querySelector("slot:not([name])");if(i&&i.assignedElements().length>0){X(i.assignedElements()[0],e);return}}X(o,e);return}const s=e.messageHtml?e.messageHtml:`${e.message} <p>Component: ${e.product.name} ${e.product.version}</p>`.replace(/https:([^ ]*)/g,"<a href='https:$1'>https:$1</a>");t.isConnected&&(t.outerHTML=`<no-license style="display:flex;align-items:center;text-align:center;justify-content:center;"><div>${s}</div></no-license>`)},G={},Ye={},j={},rt={},N=t=>`${t.name}_${t.version}`,Je=t=>{const{cvdlName:e,version:o}=t.constructor,s={name:e,version:o},i=t.tagName.toLowerCase();G[e]=G[e]??[],G[e].push(i);const r=j[N(s)];r&&setTimeout(()=>X(t,r),Te),j[N(s)]||rt[N(s)]||Ye[N(s)]||(Ye[N(s)]=!0,window.Vaadin.devTools.checkLicense(s))},Vo=t=>{rt[N(t)]=!0,console.debug("License check ok for",t)},nt=t=>{const e=t.product.name;j[N(t.product)]=t,console.error("License check failed for",e);const o=G[e];(o==null?void 0:o.length)>0&&Ne(document,o).forEach(s=>{setTimeout(()=>X(s,j[N(t.product)]),Te)})},Mo=t=>{const e=t.message,o=t.product.name;t.messageHtml=`No license found. <a target=_blank onclick="javascript:window.open(this.href);return false;" href="${e}">Go here to start a trial or retrieve your license.</a>`,j[N(t.product)]=t,console.error("No license found when checking",o);const s=G[o];(s==null?void 0:s.length)>0&&Ne(document,s).forEach(i=>{setTimeout(()=>X(i,j[N(t.product)]),Te)})},Do=()=>{window.Vaadin.devTools.createdCvdlElements.forEach(t=>{Je(t)}),window.Vaadin.devTools.createdCvdlElements={push:t=>{Je(t)}}};var _=(t=>(t.ACTIVE="active",t.INACTIVE="inactive",t.UNAVAILABLE="unavailable",t.ERROR="error",t))(_||{});const at=class extends Object{constructor(t){super(),this.status="unavailable",t&&(this.webSocket=new WebSocket(t),this.webSocket.onmessage=e=>this.handleMessage(e),this.webSocket.onerror=e=>this.handleError(e),this.webSocket.onclose=e=>{this.status!=="error"&&this.setStatus("unavailable"),this.webSocket=void 0}),setInterval(()=>{this.webSocket&&self.status!=="error"&&this.status!=="unavailable"&&this.webSocket.send("")},at.HEARTBEAT_INTERVAL)}onHandshake(){}onReload(){}onUpdate(t,e){}onConnectionError(t){}onStatusChange(t){}onMessage(t){console.error("Unknown message received from the live reload server:",t)}handleMessage(t){let e;try{e=JSON.parse(t.data)}catch(o){this.handleError(`[${o.name}: ${o.message}`);return}e.command==="hello"?(this.setStatus("active"),this.onHandshake()):e.command==="reload"?this.status==="active"&&this.onReload():e.command==="update"?this.status==="active"&&this.onUpdate(e.path,e.content):e.command==="license-check-ok"?Vo(e.data):e.command==="license-check-failed"?nt(e.data):e.command==="license-check-nokey"?Mo(e.data):this.onMessage(e)}handleError(t){console.error(t),this.setStatus("error"),t instanceof Event&&this.webSocket?this.onConnectionError(`Error in WebSocket connection to ${this.webSocket.url}`):this.onConnectionError(t)}setActive(t){!t&&this.status==="active"?this.setStatus("inactive"):t&&this.status==="inactive"&&this.setStatus("active")}setStatus(t){this.status!==t&&(this.status=t,this.onStatusChange(t))}send(t,e){const o=JSON.stringify({command:t,data:e});this.webSocket?this.webSocket.readyState!==WebSocket.OPEN?this.webSocket.addEventListener("open",()=>this.webSocket.send(o)):this.webSocket.send(o):console.error(`Unable to send message ${t}. No websocket is available`)}setFeature(t,e){this.send("setFeature",{featureId:t,enabled:e})}sendTelemetry(t){this.send("reportTelemetry",{browserData:t})}sendLicenseCheck(t){this.send("checkLicense",t)}sendShowComponentCreateLocation(t){this.send("showComponentCreateLocation",t)}sendShowComponentAttachLocation(t){this.send("showComponentAttachLocation",t)}};let ie=at;ie.HEARTBEAT_INTERVAL=18e4;var zo=Object.defineProperty,Uo=Object.getOwnPropertyDescriptor,b=(t,e,o,s)=>{for(var i=s>1?void 0:s?Uo(e,o):e,r=t.length-1,n;r>=0;r--)(n=t[r])&&(i=(s?n(e,o,i):n(i))||i);return s&&i&&zo(e,o,i),i};const v=class extends ${constructor(){super(),this.expanded=!1,this.messages=[],this.notifications=[],this.frontendStatus=_.UNAVAILABLE,this.javaStatus=_.UNAVAILABLE,this.tabs=[{id:"log",title:"Log",render:()=>this.renderLog(),activate:this.activateLog},{id:"info",title:"Info",render:()=>this.renderInfo()},{id:"features",title:"Feature Flags",render:()=>this.renderFeatures()}],this.activeTab="log",this.serverInfo={flowVersion:"",vaadinVersion:"",javaVersion:"",osVersion:"",productName:""},this.features=[],this.unreadErrors=!1,this.componentPickActive=!1,this.themeEditorState=W.disabled,this.nextMessageId=1,this.transitionDuration=0,this.disableLiveReloadTimeout=null,window.Vaadin.Flow&&this.tabs.push({id:"code",title:"Code",render:()=>this.renderCode()})}static get styles(){return[g`
        :host {
          --dev-tools-font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen-Sans, Ubuntu, Cantarell,
            'Helvetica Neue', sans-serif;
          --dev-tools-font-family-monospace: SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New',
            monospace;

          --dev-tools-font-size: 0.8125rem;
          --dev-tools-font-size-small: 0.75rem;

          --dev-tools-text-color: rgba(255, 255, 255, 0.8);
          --dev-tools-text-color-secondary: rgba(255, 255, 255, 0.65);
          --dev-tools-text-color-emphasis: rgba(255, 255, 255, 0.95);
          --dev-tools-text-color-active: rgba(255, 255, 255, 1);

          --dev-tools-background-color-inactive: rgba(45, 45, 45, 0.25);
          --dev-tools-background-color-active: rgba(45, 45, 45, 0.98);
          --dev-tools-background-color-active-blurred: rgba(45, 45, 45, 0.85);

          --dev-tools-border-radius: 0.5rem;
          --dev-tools-box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.05), 0 4px 12px -2px rgba(0, 0, 0, 0.4);

          --dev-tools-blue-hsl: 206, 100%, 70%;
          --dev-tools-blue-color: hsl(var(--dev-tools-blue-hsl));
          --dev-tools-green-hsl: 145, 80%, 42%;
          --dev-tools-green-color: hsl(var(--dev-tools-green-hsl));
          --dev-tools-grey-hsl: 0, 0%, 50%;
          --dev-tools-grey-color: hsl(var(--dev-tools-grey-hsl));
          --dev-tools-yellow-hsl: 38, 98%, 64%;
          --dev-tools-yellow-color: hsl(var(--dev-tools-yellow-hsl));
          --dev-tools-red-hsl: 355, 100%, 68%;
          --dev-tools-red-color: hsl(var(--dev-tools-red-hsl));

          /* Needs to be in ms, used in JavaScript as well */
          --dev-tools-transition-duration: 180ms;

          all: initial;

          direction: ltr;
          cursor: default;
          font: normal 400 var(--dev-tools-font-size) / 1.125rem var(--dev-tools-font-family);
          color: var(--dev-tools-text-color);
          -webkit-user-select: none;
          -moz-user-select: none;
          user-select: none;

          position: fixed;
          z-index: 20000;
          pointer-events: none;
          bottom: 0;
          right: 0;
          width: 100%;
          height: 100%;
          display: flex;
          flex-direction: column-reverse;
          align-items: flex-end;
        }

        .dev-tools {
          pointer-events: auto;
          display: flex;
          align-items: center;
          position: fixed;
          z-index: inherit;
          right: 0.5rem;
          bottom: 0.5rem;
          min-width: 1.75rem;
          height: 1.75rem;
          max-width: 1.75rem;
          border-radius: 0.5rem;
          padding: 0.375rem;
          box-sizing: border-box;
          background-color: var(--dev-tools-background-color-inactive);
          box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.05);
          color: var(--dev-tools-text-color);
          transition: var(--dev-tools-transition-duration);
          white-space: nowrap;
          line-height: 1rem;
        }

        .dev-tools:hover,
        .dev-tools.active {
          background-color: var(--dev-tools-background-color-active);
          box-shadow: var(--dev-tools-box-shadow);
        }

        .dev-tools.active {
          max-width: calc(100% - 1rem);
        }

        .dev-tools .dev-tools-icon {
          flex: none;
          pointer-events: none;
          display: inline-block;
          width: 1rem;
          height: 1rem;
          fill: #fff;
          transition: var(--dev-tools-transition-duration);
          margin: 0;
        }

        .dev-tools.active .dev-tools-icon {
          opacity: 0;
          position: absolute;
          transform: scale(0.5);
        }

        .dev-tools .status-blip {
          flex: none;
          display: block;
          width: 6px;
          height: 6px;
          border-radius: 50%;
          z-index: 20001;
          background: var(--dev-tools-grey-color);
          position: absolute;
          top: -1px;
          right: -1px;
        }

        .dev-tools .status-description {
          overflow: hidden;
          text-overflow: ellipsis;
          padding: 0 0.25rem;
        }

        .dev-tools.error {
          background-color: hsla(var(--dev-tools-red-hsl), 0.15);
          animation: bounce 0.5s;
          animation-iteration-count: 2;
        }

        .switch {
          display: inline-flex;
          align-items: center;
        }

        .switch input {
          opacity: 0;
          width: 0;
          height: 0;
          position: absolute;
        }

        .switch .slider {
          display: block;
          flex: none;
          width: 28px;
          height: 18px;
          border-radius: 9px;
          background-color: rgba(255, 255, 255, 0.3);
          transition: var(--dev-tools-transition-duration);
          margin-right: 0.5rem;
        }

        .switch:focus-within .slider,
        .switch .slider:hover {
          background-color: rgba(255, 255, 255, 0.35);
          transition: none;
        }

        .switch input:focus-visible ~ .slider {
          box-shadow: 0 0 0 2px var(--dev-tools-background-color-active), 0 0 0 4px var(--dev-tools-blue-color);
        }

        .switch .slider::before {
          content: '';
          display: block;
          margin: 2px;
          width: 14px;
          height: 14px;
          background-color: #fff;
          transition: var(--dev-tools-transition-duration);
          border-radius: 50%;
        }

        .switch input:checked + .slider {
          background-color: var(--dev-tools-green-color);
        }

        .switch input:checked + .slider::before {
          transform: translateX(10px);
        }

        .switch input:disabled + .slider::before {
          background-color: var(--dev-tools-grey-color);
        }

        .window.hidden {
          opacity: 0;
          transform: scale(0);
          position: absolute;
        }

        .window.visible {
          transform: none;
          opacity: 1;
          pointer-events: auto;
        }

        .window.visible ~ .dev-tools {
          opacity: 0;
          pointer-events: none;
        }

        .window.visible ~ .dev-tools .dev-tools-icon,
        .window.visible ~ .dev-tools .status-blip {
          transition: none;
          opacity: 0;
        }

        .window {
          border-radius: var(--dev-tools-border-radius);
          overflow: hidden;
          margin: 0.5rem;
          width: 30rem;
          max-width: calc(100% - 1rem);
          max-height: calc(100vh - 1rem);
          flex-shrink: 1;
          background-color: var(--dev-tools-background-color-active);
          color: var(--dev-tools-text-color);
          transition: var(--dev-tools-transition-duration);
          transform-origin: bottom right;
          display: flex;
          flex-direction: column;
          box-shadow: var(--dev-tools-box-shadow);
          outline: none;
        }

        .window-toolbar {
          display: flex;
          flex: none;
          align-items: center;
          padding: 0.375rem;
          white-space: nowrap;
          order: 1;
          background-color: rgba(0, 0, 0, 0.2);
          gap: 0.5rem;
        }

        .tab {
          color: var(--dev-tools-text-color-secondary);
          font: inherit;
          font-size: var(--dev-tools-font-size-small);
          font-weight: 500;
          line-height: 1;
          padding: 0.25rem 0.375rem;
          background: none;
          border: none;
          margin: 0;
          border-radius: 0.25rem;
          transition: var(--dev-tools-transition-duration);
        }

        .tab:hover,
        .tab.active {
          color: var(--dev-tools-text-color-active);
        }

        .tab.active {
          background-color: rgba(255, 255, 255, 0.12);
        }

        .tab.unreadErrors::after {
          content: '•';
          color: hsl(var(--dev-tools-red-hsl));
          font-size: 1.5rem;
          position: absolute;
          transform: translate(0, -50%);
        }

        .ahreflike {
          font-weight: 500;
          color: var(--dev-tools-text-color-secondary);
          text-decoration: underline;
          cursor: pointer;
        }

        .ahreflike:hover {
          color: var(--dev-tools-text-color-emphasis);
        }

        .button {
          all: initial;
          font-family: inherit;
          font-size: var(--dev-tools-font-size-small);
          line-height: 1;
          white-space: nowrap;
          background-color: rgba(0, 0, 0, 0.2);
          color: inherit;
          font-weight: 600;
          padding: 0.25rem 0.375rem;
          border-radius: 0.25rem;
        }

        .button:focus,
        .button:hover {
          color: var(--dev-tools-text-color-emphasis);
        }

        .minimize-button {
          flex: none;
          width: 1rem;
          height: 1rem;
          color: inherit;
          background-color: transparent;
          border: 0;
          padding: 0;
          margin: 0 0 0 auto;
          opacity: 0.8;
        }

        .minimize-button:hover {
          opacity: 1;
        }

        .minimize-button svg {
          max-width: 100%;
        }

        .message.information {
          --dev-tools-notification-color: var(--dev-tools-blue-color);
        }

        .message.warning {
          --dev-tools-notification-color: var(--dev-tools-yellow-color);
        }

        .message.error {
          --dev-tools-notification-color: var(--dev-tools-red-color);
        }

        .message {
          display: flex;
          padding: 0.1875rem 0.75rem 0.1875rem 2rem;
          background-clip: padding-box;
        }

        .message.log {
          padding-left: 0.75rem;
        }

        .message-content {
          margin-right: 0.5rem;
          -webkit-user-select: text;
          -moz-user-select: text;
          user-select: text;
        }

        .message-heading {
          position: relative;
          display: flex;
          align-items: center;
          margin: 0.125rem 0;
        }

        .message.log {
          color: var(--dev-tools-text-color-secondary);
        }

        .message:not(.log) .message-heading {
          font-weight: 500;
        }

        .message.has-details .message-heading {
          color: var(--dev-tools-text-color-emphasis);
          font-weight: 600;
        }

        .message-heading::before {
          position: absolute;
          margin-left: -1.5rem;
          display: inline-block;
          text-align: center;
          font-size: 0.875em;
          font-weight: 600;
          line-height: calc(1.25em - 2px);
          width: 14px;
          height: 14px;
          box-sizing: border-box;
          border: 1px solid transparent;
          border-radius: 50%;
        }

        .message.information .message-heading::before {
          content: 'i';
          border-color: currentColor;
          color: var(--dev-tools-notification-color);
        }

        .message.warning .message-heading::before,
        .message.error .message-heading::before {
          content: '!';
          color: var(--dev-tools-background-color-active);
          background-color: var(--dev-tools-notification-color);
        }

        .features-tray {
          padding: 0.75rem;
          flex: auto;
          overflow: auto;
          animation: fade-in var(--dev-tools-transition-duration) ease-in;
          user-select: text;
        }

        .features-tray p {
          margin-top: 0;
          color: var(--dev-tools-text-color-secondary);
        }

        .features-tray .feature {
          display: flex;
          align-items: center;
          gap: 1rem;
          padding-bottom: 0.5em;
        }

        .message .message-details {
          font-weight: 400;
          color: var(--dev-tools-text-color-secondary);
          margin: 0.25rem 0;
        }

        .message .message-details[hidden] {
          display: none;
        }

        .message .message-details p {
          display: inline;
          margin: 0;
          margin-right: 0.375em;
          word-break: break-word;
        }

        .message .persist {
          color: var(--dev-tools-text-color-secondary);
          white-space: nowrap;
          margin: 0.375rem 0;
          display: flex;
          align-items: center;
          position: relative;
          -webkit-user-select: none;
          -moz-user-select: none;
          user-select: none;
        }

        .message .persist::before {
          content: '';
          width: 1em;
          height: 1em;
          border-radius: 0.2em;
          margin-right: 0.375em;
          background-color: rgba(255, 255, 255, 0.3);
        }

        .message .persist:hover::before {
          background-color: rgba(255, 255, 255, 0.4);
        }

        .message .persist.on::before {
          background-color: rgba(255, 255, 255, 0.9);
        }

        .message .persist.on::after {
          content: '';
          order: -1;
          position: absolute;
          width: 0.75em;
          height: 0.25em;
          border: 2px solid var(--dev-tools-background-color-active);
          border-width: 0 0 2px 2px;
          transform: translate(0.05em, -0.05em) rotate(-45deg) scale(0.8, 0.9);
        }

        .message .dismiss-message {
          font-weight: 600;
          align-self: stretch;
          display: flex;
          align-items: center;
          padding: 0 0.25rem;
          margin-left: 0.5rem;
          color: var(--dev-tools-text-color-secondary);
        }

        .message .dismiss-message:hover {
          color: var(--dev-tools-text-color);
        }

        .notification-tray {
          display: flex;
          flex-direction: column-reverse;
          align-items: flex-end;
          margin: 0.5rem;
          flex: none;
        }

        .window.hidden + .notification-tray {
          margin-bottom: 3rem;
        }

        .notification-tray .message {
          pointer-events: auto;
          background-color: var(--dev-tools-background-color-active);
          color: var(--dev-tools-text-color);
          max-width: 30rem;
          box-sizing: border-box;
          border-radius: var(--dev-tools-border-radius);
          margin-top: 0.5rem;
          transition: var(--dev-tools-transition-duration);
          transform-origin: bottom right;
          animation: slideIn var(--dev-tools-transition-duration);
          box-shadow: var(--dev-tools-box-shadow);
          padding-top: 0.25rem;
          padding-bottom: 0.25rem;
        }

        .notification-tray .message.animate-out {
          animation: slideOut forwards var(--dev-tools-transition-duration);
        }

        .notification-tray .message .message-details {
          max-height: 10em;
          overflow: hidden;
        }

        .message-tray {
          flex: auto;
          overflow: auto;
          max-height: 20rem;
          user-select: text;
        }

        .message-tray .message {
          animation: fade-in var(--dev-tools-transition-duration) ease-in;
          padding-left: 2.25rem;
        }

        .message-tray .message.warning {
          background-color: hsla(var(--dev-tools-yellow-hsl), 0.09);
        }

        .message-tray .message.error {
          background-color: hsla(var(--dev-tools-red-hsl), 0.09);
        }

        .message-tray .message.error .message-heading {
          color: hsl(var(--dev-tools-red-hsl));
        }

        .message-tray .message.warning .message-heading {
          color: hsl(var(--dev-tools-yellow-hsl));
        }

        .message-tray .message + .message {
          border-top: 1px solid rgba(255, 255, 255, 0.07);
        }

        .message-tray .dismiss-message,
        .message-tray .persist {
          display: none;
        }

        .info-tray {
          padding: 0.75rem;
          position: relative;
          flex: auto;
          overflow: auto;
          animation: fade-in var(--dev-tools-transition-duration) ease-in;
          user-select: text;
        }

        .info-tray dl {
          margin: 0;
          display: grid;
          grid-template-columns: max-content 1fr;
          column-gap: 0.75rem;
          position: relative;
        }

        .info-tray dt {
          grid-column: 1;
          color: var(--dev-tools-text-color-emphasis);
        }

        .info-tray dt:not(:first-child)::before {
          content: '';
          width: 100%;
          position: absolute;
          height: 1px;
          background-color: rgba(255, 255, 255, 0.1);
          margin-top: -0.375rem;
        }

        .info-tray dd {
          grid-column: 2;
          margin: 0;
        }

        .info-tray :is(dt, dd):not(:last-child) {
          margin-bottom: 0.75rem;
        }

        .info-tray dd + dd {
          margin-top: -0.5rem;
        }

        .info-tray .live-reload-status::before {
          content: '•';
          color: var(--status-color);
          width: 0.75rem;
          display: inline-block;
          font-size: 1rem;
          line-height: 0.5rem;
        }

        .info-tray .copy {
          position: fixed;
          z-index: 1;
          top: 0.5rem;
          right: 0.5rem;
        }

        .info-tray .switch {
          vertical-align: -4px;
        }

        @keyframes slideIn {
          from {
            transform: translateX(100%);
            opacity: 0;
          }
          to {
            transform: translateX(0%);
            opacity: 1;
          }
        }

        @keyframes slideOut {
          from {
            transform: translateX(0%);
            opacity: 1;
          }
          to {
            transform: translateX(100%);
            opacity: 0;
          }
        }

        @keyframes fade-in {
          0% {
            opacity: 0;
          }
        }

        @keyframes bounce {
          0% {
            transform: scale(0.8);
          }
          50% {
            transform: scale(1.5);
            background-color: hsla(var(--dev-tools-red-hsl), 1);
          }
          100% {
            transform: scale(1);
          }
        }

        @supports (backdrop-filter: blur(1px)) {
          .dev-tools,
          .window,
          .notification-tray .message {
            backdrop-filter: blur(8px);
          }
          .dev-tools:hover,
          .dev-tools.active,
          .window,
          .notification-tray .message {
            background-color: var(--dev-tools-background-color-active-blurred);
          }
        }
      `,it]}static get isActive(){const t=window.sessionStorage.getItem(v.ACTIVE_KEY_IN_SESSION_STORAGE);return t===null||t!=="false"}static notificationDismissed(t){const e=window.localStorage.getItem(v.DISMISSED_NOTIFICATIONS_IN_LOCAL_STORAGE);return e!==null&&e.includes(t)}elementTelemetry(){let t={};try{const e=localStorage.getItem("vaadin.statistics.basket");if(!e)return;t=JSON.parse(e)}catch{return}this.frontendConnection&&this.frontendConnection.sendTelemetry(t)}openWebSocketConnection(){this.frontendStatus=_.UNAVAILABLE,this.javaStatus=_.UNAVAILABLE;const t=a=>this.log("error",a),e=()=>{this.showSplashMessage("Reloading…");const a=window.sessionStorage.getItem(v.TRIGGERED_COUNT_KEY_IN_SESSION_STORAGE),d=a?parseInt(a,10)+1:1;window.sessionStorage.setItem(v.TRIGGERED_COUNT_KEY_IN_SESSION_STORAGE,d.toString()),window.sessionStorage.setItem(v.TRIGGERED_KEY_IN_SESSION_STORAGE,"true"),window.location.reload()},o=(a,d)=>{let c=document.head.querySelector(`style[data-file-path='${a}']`);c?(this.log("information","Hot update of "+a),c.textContent=d,document.dispatchEvent(new CustomEvent("vaadin-theme-updated"))):e()},s=new ie(this.getDedicatedWebSocketUrl());s.onHandshake=()=>{this.log("log","Vaadin development mode initialized"),v.isActive||s.setActive(!1),this.elementTelemetry()},s.onConnectionError=t,s.onReload=e,s.onUpdate=o,s.onStatusChange=a=>{this.frontendStatus=a},s.onMessage=a=>this.handleFrontendMessage(a),this.frontendConnection=s;let i;this.backend===v.SPRING_BOOT_DEVTOOLS&&this.springBootLiveReloadPort?(i=new ie(this.getSpringBootWebSocketUrl(window.location)),i.onHandshake=()=>{v.isActive||i.setActive(!1)},i.onReload=e,i.onConnectionError=t):this.backend===v.JREBEL||this.backend===v.HOTSWAP_AGENT?i=s:i=new ie(void 0);const r=i.onStatusChange;i.onStatusChange=a=>{r(a),this.javaStatus=a};const n=i.onHandshake;i.onHandshake=()=>{n(),this.backend&&this.log("information",`Java live reload available: ${v.BACKEND_DISPLAY_NAME[this.backend]}`)},this.javaConnection=i,this.backend||this.showNotification("warning","Java live reload unavailable","Live reload for Java changes is currently not set up. Find out how to make use of this functionality to boost your workflow.","https://vaadin.com/docs/latest/flow/configuration/live-reload","liveReloadUnavailable")}handleFrontendMessage(t){if((t==null?void 0:t.command)==="serverInfo")this.serverInfo=t.data;else if((t==null?void 0:t.command)==="featureFlags")this.features=t.data.features;else if((t==null?void 0:t.command)==="themeEditorState"){const e=!!window.Vaadin.Flow;this.themeEditorState=t.data,e&&this.themeEditorState!==W.disabled&&(this.tabs.push({id:"theme-editor",title:"Theme Editor (Free Preview)",render:()=>this.renderThemeEditor()}),this.requestUpdate())}else console.error("Unknown message from front-end connection:",JSON.stringify(t))}getDedicatedWebSocketUrl(){function t(o){const s=document.createElement("div");return s.innerHTML=`<a href="${o}"/>`,s.firstChild.href}if(this.url===void 0)return;const e=t(this.url);if(!e.startsWith("http://")&&!e.startsWith("https://")){console.error("The protocol of the url should be http or https for live reload to work.");return}return`${e.replace(/^http/,"ws")}?v-r=push&debug_window`}getSpringBootWebSocketUrl(t){const{hostname:e}=t,o=t.protocol==="https:"?"wss":"ws";if(e.endsWith("gitpod.io")){const s=e.replace(/.*?-/,"");return`${o}://${this.springBootLiveReloadPort}-${s}`}else return`${o}://${e}:${this.springBootLiveReloadPort}`}connectedCallback(){if(super.connectedCallback(),this.catchErrors(),this.disableEventListener=o=>this.demoteSplashMessage(),document.body.addEventListener("focus",this.disableEventListener),document.body.addEventListener("click",this.disableEventListener),this.openWebSocketConnection(),window.sessionStorage.getItem(v.TRIGGERED_KEY_IN_SESSION_STORAGE)){const o=new Date,s=`${`0${o.getHours()}`.slice(-2)}:${`0${o.getMinutes()}`.slice(-2)}:${`0${o.getSeconds()}`.slice(-2)}`;this.showSplashMessage(`Page reloaded at ${s}`),window.sessionStorage.removeItem(v.TRIGGERED_KEY_IN_SESSION_STORAGE)}this.transitionDuration=parseInt(window.getComputedStyle(this).getPropertyValue("--dev-tools-transition-duration"),10);const e=window;e.Vaadin=e.Vaadin||{},e.Vaadin.devTools=Object.assign(this,e.Vaadin.devTools),Do(),document.documentElement.addEventListener("vaadin-overlay-outside-click",o=>{const s=o,i=s.target.owner;if(i?ut(this,i):!1)return;s.detail.sourceEvent.composedPath().includes(this)&&o.preventDefault()})}format(t){return t.toString()}catchErrors(){const t=window.Vaadin.ConsoleErrors;t&&t.forEach(e=>{this.log("error",e.map(o=>this.format(o)).join(" "))}),window.Vaadin.ConsoleErrors={push:e=>{this.log("error",e.map(o=>this.format(o)).join(" "))}}}disconnectedCallback(){this.disableEventListener&&(document.body.removeEventListener("focus",this.disableEventListener),document.body.removeEventListener("click",this.disableEventListener)),super.disconnectedCallback()}toggleExpanded(){this.notifications.slice().forEach(t=>this.dismissNotification(t.id)),this.expanded=!this.expanded,this.expanded&&this.root.focus()}showSplashMessage(t){this.splashMessage=t,this.splashMessage&&(this.expanded?this.demoteSplashMessage():setTimeout(()=>{this.demoteSplashMessage()},v.AUTO_DEMOTE_NOTIFICATION_DELAY))}demoteSplashMessage(){this.splashMessage&&this.log("log",this.splashMessage),this.showSplashMessage(void 0)}checkLicense(t){this.frontendConnection?this.frontendConnection.sendLicenseCheck(t):nt({message:"Internal error: no connection",product:t})}log(t,e,o,s){const i=this.nextMessageId;for(this.nextMessageId+=1,this.messages.push({id:i,type:t,message:e,details:o,link:s,dontShowAgain:!1,deleted:!1});this.messages.length>v.MAX_LOG_ROWS;)this.messages.shift();this.requestUpdate(),this.updateComplete.then(()=>{const r=this.renderRoot.querySelector(".message-tray .message:last-child");this.expanded&&r?(setTimeout(()=>r.scrollIntoView({behavior:"smooth"}),this.transitionDuration),this.unreadErrors=!1):t==="error"&&(this.unreadErrors=!0)})}showNotification(t,e,o,s,i){if(i===void 0||!v.notificationDismissed(i)){if(this.notifications.filter(a=>a.persistentId===i).filter(a=>!a.deleted).length>0)return;const n=this.nextMessageId;this.nextMessageId+=1,this.notifications.push({id:n,type:t,message:e,details:o,link:s,persistentId:i,dontShowAgain:!1,deleted:!1}),s===void 0&&setTimeout(()=>{this.dismissNotification(n)},v.AUTO_DEMOTE_NOTIFICATION_DELAY),this.requestUpdate()}else this.log(t,e,o,s)}dismissNotification(t){const e=this.findNotificationIndex(t);if(e!==-1&&!this.notifications[e].deleted){const o=this.notifications[e];if(o.dontShowAgain&&o.persistentId&&!v.notificationDismissed(o.persistentId)){let s=window.localStorage.getItem(v.DISMISSED_NOTIFICATIONS_IN_LOCAL_STORAGE);s=s===null?o.persistentId:`${s},${o.persistentId}`,window.localStorage.setItem(v.DISMISSED_NOTIFICATIONS_IN_LOCAL_STORAGE,s)}o.deleted=!0,this.log(o.type,o.message,o.details,o.link),setTimeout(()=>{const s=this.findNotificationIndex(t);s!==-1&&(this.notifications.splice(s,1),this.requestUpdate())},this.transitionDuration)}}findNotificationIndex(t){let e=-1;return this.notifications.some((o,s)=>o.id===t?(e=s,!0):!1),e}toggleDontShowAgain(t){const e=this.findNotificationIndex(t);if(e!==-1&&!this.notifications[e].deleted){const o=this.notifications[e];o.dontShowAgain=!o.dontShowAgain,this.requestUpdate()}}setActive(t){var e,o;(e=this.frontendConnection)==null||e.setActive(t),(o=this.javaConnection)==null||o.setActive(t),window.sessionStorage.setItem(v.ACTIVE_KEY_IN_SESSION_STORAGE,t?"true":"false")}getStatusColor(t){return t===_.ACTIVE?"var(--dev-tools-green-color)":t===_.INACTIVE?"var(--dev-tools-grey-color)":t===_.UNAVAILABLE?"var(--dev-tools-yellow-hsl)":t===_.ERROR?"var(--dev-tools-red-color)":"none"}renderMessage(t){return l`
      <div
        class="message ${t.type} ${t.deleted?"animate-out":""} ${t.details||t.link?"has-details":""}"
      >
        <div class="message-content">
          <div class="message-heading">${t.message}</div>
          <div class="message-details" ?hidden="${!t.details&&!t.link}">
            ${t.details?l`<p>${t.details}</p>`:""}
            ${t.link?l`<a class="ahreflike" href="${t.link}" target="_blank">Learn more</a>`:""}
          </div>
          ${t.persistentId?l`<div
                class="persist ${t.dontShowAgain?"on":"off"}"
                @click=${()=>this.toggleDontShowAgain(t.id)}
              >
                Don’t show again
              </div>`:""}
        </div>
        <div class="dismiss-message" @click=${()=>this.dismissNotification(t.id)}>Dismiss</div>
      </div>
    `}render(){return l` <div
        class="window ${this.expanded&&!this.componentPickActive?"visible":"hidden"}"
        tabindex="0"
        @keydown=${t=>t.key==="Escape"&&this.expanded&&this.toggleExpanded()}
      >
        <div class="window-toolbar">
          ${this.tabs.map(t=>l`<button
                class=${Ze({tab:!0,active:this.activeTab===t.id,unreadErrors:t.id==="log"&&this.unreadErrors})}
                id="${t.id}"
                @click=${()=>{this.activeTab=t.id,t.activate&&t.activate.call(this)}}
              >
                ${t.title}
              </button> `)}
          <button class="minimize-button" title="Minimize" @click=${()=>this.toggleExpanded()}>
            <svg fill="none" height="16" viewBox="0 0 16 16" width="16" xmlns="http://www.w3.org/2000/svg">
              <g fill="#fff" opacity=".8">
                <path
                  d="m7.25 1.75c0-.41421.33579-.75.75-.75h3.25c2.0711 0 3.75 1.67893 3.75 3.75v6.5c0 2.0711-1.6789 3.75-3.75 3.75h-6.5c-2.07107 0-3.75-1.6789-3.75-3.75v-3.25c0-.41421.33579-.75.75-.75s.75.33579.75.75v3.25c0 1.2426 1.00736 2.25 2.25 2.25h6.5c1.2426 0 2.25-1.0074 2.25-2.25v-6.5c0-1.24264-1.0074-2.25-2.25-2.25h-3.25c-.41421 0-.75-.33579-.75-.75z"
                />
                <path
                  d="m2.96967 2.96967c.29289-.29289.76777-.29289 1.06066 0l5.46967 5.46967v-2.68934c0-.41421.33579-.75.75-.75.4142 0 .75.33579.75.75v4.5c0 .4142-.3358.75-.75.75h-4.5c-.41421 0-.75-.3358-.75-.75 0-.41421.33579-.75.75-.75h2.68934l-5.46967-5.46967c-.29289-.29289-.29289-.76777 0-1.06066z"
                />
              </g>
            </svg>
          </button>
        </div>
        ${this.tabs.map(t=>this.activeTab===t.id?t.render():Ie)}
      </div>

      <div class="notification-tray">${this.notifications.map(t=>this.renderMessage(t))}</div>
      <vaadin-dev-tools-component-picker
        .active=${this.componentPickActive}
        @component-picker-opened=${()=>{this.componentPickActive=!0}}
        @component-picker-closed=${()=>{this.componentPickActive=!1}}
      ></vaadin-dev-tools-component-picker>
      <div
        class="dev-tools ${this.splashMessage?"active":""}${this.unreadErrors?" error":""}"
        @click=${()=>this.toggleExpanded()}
      >
        ${this.unreadErrors?l`<svg
              fill="none"
              height="16"
              viewBox="0 0 16 16"
              width="16"
              xmlns="http://www.w3.org/2000/svg"
              xmlns:xlink="http://www.w3.org/1999/xlink"
              class="dev-tools-icon error"
            >
              <clipPath id="a"><path d="m0 0h16v16h-16z" /></clipPath>
              <g clip-path="url(#a)">
                <path
                  d="m6.25685 2.09894c.76461-1.359306 2.72169-1.359308 3.4863 0l5.58035 9.92056c.7499 1.3332-.2135 2.9805-1.7432 2.9805h-11.1606c-1.529658 0-2.4930857-1.6473-1.743156-2.9805z"
                  fill="#ff5c69"
                />
                <path
                  d="m7.99699 4c-.45693 0-.82368.37726-.81077.834l.09533 3.37352c.01094.38726.32803.69551.71544.69551.38741 0 .70449-.30825.71544-.69551l.09533-3.37352c.0129-.45674-.35384-.834-.81077-.834zm.00301 8c.60843 0 1-.3879 1-.979 0-.5972-.39157-.9851-1-.9851s-1 .3879-1 .9851c0 .5911.39157.979 1 .979z"
                  fill="#fff"
                />
              </g>
            </svg>`:l`<svg
              fill="none"
              height="17"
              viewBox="0 0 16 17"
              width="16"
              xmlns="http://www.w3.org/2000/svg"
              class="dev-tools-icon logo"
            >
              <g fill="#fff">
                <path
                  d="m8.88273 5.97926c0 .04401-.0032.08898-.00801.12913-.02467.42848-.37813.76767-.8117.76767-.43358 0-.78704-.34112-.81171-.76928-.00481-.04015-.00801-.08351-.00801-.12752 0-.42784-.10255-.87656-1.14434-.87656h-3.48364c-1.57118 0-2.315271-.72849-2.315271-2.21758v-1.26683c0-.42431.324618-.768314.748261-.768314.42331 0 .74441.344004.74441.768314v.42784c0 .47924.39576.81265 1.11293.81265h3.41538c1.5542 0 1.67373 1.156 1.725 1.7679h.03429c.05095-.6119.17048-1.7679 1.72468-1.7679h3.4154c.7172 0 1.0145-.32924 1.0145-.80847l-.0067-.43202c0-.42431.3227-.768314.7463-.768314.4234 0 .7255.344004.7255.768314v1.26683c0 1.48909-.6181 2.21758-2.1893 2.21758h-3.4836c-1.04182 0-1.14437.44872-1.14437.87656z"
                />
                <path
                  d="m8.82577 15.1648c-.14311.3144-.4588.5335-.82635.5335-.37268 0-.69252-.2249-.83244-.5466-.00206-.0037-.00412-.0073-.00617-.0108-.00275-.0047-.00549-.0094-.00824-.0145l-3.16998-5.87318c-.08773-.15366-.13383-.32816-.13383-.50395 0-.56168.45592-1.01879 1.01621-1.01879.45048 0 .75656.22069.96595.6993l2.16882 4.05042 2.17166-4.05524c.2069-.47379.513-.69448.9634-.69448.5603 0 1.0166.45711 1.0166 1.01879 0 .17579-.0465.35029-.1348.50523l-3.1697 5.8725c-.00503.0096-.01006.0184-.01509.0272-.00201.0036-.00402.0071-.00604.0106z"
                />
              </g>
            </svg>`}

        <span
          class="status-blip"
          style="background: linear-gradient(to right, ${this.getStatusColor(this.frontendStatus)} 50%, ${this.getStatusColor(this.javaStatus)} 50%)"
        ></span>
        ${this.splashMessage?l`<span class="status-description">${this.splashMessage}</span></div>`:Ie}
      </div>`}renderLog(){return l`<div class="message-tray">${this.messages.map(t=>this.renderMessage(t))}</div>`}activateLog(){this.unreadErrors=!1,this.updateComplete.then(()=>{const t=this.renderRoot.querySelector(".message-tray .message:last-child");t&&t.scrollIntoView()})}renderCode(){return l`<div class="info-tray">
      <div>
        <select id="locationType">
          <option value="create" selected>Create</option>
          <option value="attach">Attach</option>
        </select>
        <button
          class="button pick"
          @click=${async()=>{await u(()=>Promise.resolve().then(()=>No),void 0),this.componentPicker.open({infoTemplate:l`
                <div>
                  <h3>Locate a component in source code</h3>
                  <p>Use the mouse cursor to highlight components in the UI.</p>
                  <p>Use arrow down/up to cycle through and highlight specific components under the cursor.</p>
                  <p>
                    Click the primary mouse button to open the corresponding source code line of the highlighted
                    component in your IDE.
                  </p>
                </div>
              `,pickCallback:t=>{const e={nodeId:t.nodeId,uiId:t.uiId};this.renderRoot.querySelector("#locationType").value==="create"?this.frontendConnection.sendShowComponentCreateLocation(e):this.frontendConnection.sendShowComponentAttachLocation(e)}})}}
        >
          Find component in code
        </button>
      </div>
      </div>
    </div>`}renderInfo(){return l`<div class="info-tray">
      <button class="button copy" @click=${this.copyInfoToClipboard}>Copy</button>
      <dl>
        <dt>${this.serverInfo.productName}</dt>
        <dd>${this.serverInfo.vaadinVersion}</dd>
        <dt>Flow</dt>
        <dd>${this.serverInfo.flowVersion}</dd>
        <dt>Java</dt>
        <dd>${this.serverInfo.javaVersion}</dd>
        <dt>OS</dt>
        <dd>${this.serverInfo.osVersion}</dd>
        <dt>Browser</dt>
        <dd>${navigator.userAgent}</dd>
        <dt>
          Live reload
          <label class="switch">
            <input
              id="toggle"
              type="checkbox"
              ?disabled=${this.liveReloadDisabled||(this.frontendStatus===_.UNAVAILABLE||this.frontendStatus===_.ERROR)&&(this.javaStatus===_.UNAVAILABLE||this.javaStatus===_.ERROR)}
              ?checked="${this.frontendStatus===_.ACTIVE||this.javaStatus===_.ACTIVE}"
              @change=${t=>this.setActive(t.target.checked)}
            />
            <span class="slider"></span>
          </label>
        </dt>
        <dd class="live-reload-status" style="--status-color: ${this.getStatusColor(this.javaStatus)}">
          Java ${this.javaStatus} ${this.backend?`(${v.BACKEND_DISPLAY_NAME[this.backend]})`:""}
        </dd>
        <dd class="live-reload-status" style="--status-color: ${this.getStatusColor(this.frontendStatus)}">
          Front end ${this.frontendStatus}
        </dd>
      </dl>
    </div>`}renderFeatures(){return l`<div class="features-tray">
      ${this.features.map(t=>l`<div class="feature">
          <label class="switch">
            <input
              class="feature-toggle"
              id="feature-toggle-${t.id}"
              type="checkbox"
              ?checked=${t.enabled}
              @change=${e=>this.toggleFeatureFlag(e,t)}
            />
            <span class="slider"></span>
            ${t.title}
          </label>
          <a class="ahreflike" href="${t.moreInfoLink}" target="_blank">Learn more</a>
        </div>`)}
    </div>`}renderThemeEditor(){return l` <vaadin-dev-tools-theme-editor
      .expanded=${this.expanded}
      .themeEditorState=${this.themeEditorState}
      .pickerProvider=${()=>this.componentPicker}
      .connection=${this.frontendConnection}
    ></vaadin-dev-tools-theme-editor>`}copyInfoToClipboard(){const t=this.renderRoot.querySelectorAll(".info-tray dt, .info-tray dd"),e=Array.from(t).map(o=>(o.localName==="dd"?": ":`
`)+o.textContent.trim()).join("").replace(/^\n/,"");Lo(e),this.showNotification("information","Environment information copied to clipboard",void 0,void 0,"versionInfoCopied")}toggleFeatureFlag(t,e){const o=t.target.checked;this.frontendConnection?(this.frontendConnection.setFeature(e.id,o),this.showNotification("information",`“${e.title}” ${o?"enabled":"disabled"}`,e.requiresServerRestart?"This feature requires a server restart":void 0,void 0,`feature${e.id}${o?"Enabled":"Disabled"}`)):this.log("error",`Unable to toggle feature ${e.title}: No server connection available`)}};let h=v;h.MAX_LOG_ROWS=1e3;h.DISMISSED_NOTIFICATIONS_IN_LOCAL_STORAGE="vaadin.live-reload.dismissedNotifications";h.ACTIVE_KEY_IN_SESSION_STORAGE="vaadin.live-reload.active";h.TRIGGERED_KEY_IN_SESSION_STORAGE="vaadin.live-reload.triggered";h.TRIGGERED_COUNT_KEY_IN_SESSION_STORAGE="vaadin.live-reload.triggeredCount";h.AUTO_DEMOTE_NOTIFICATION_DELAY=5e3;h.HOTSWAP_AGENT="HOTSWAP_AGENT";h.JREBEL="JREBEL";h.SPRING_BOOT_DEVTOOLS="SPRING_BOOT_DEVTOOLS";h.BACKEND_DISPLAY_NAME={HOTSWAP_AGENT:"HotswapAgent",JREBEL:"JRebel",SPRING_BOOT_DEVTOOLS:"Spring Boot Devtools"};b([p({type:String})],h.prototype,"url",2);b([p({type:Boolean,attribute:!0})],h.prototype,"liveReloadDisabled",2);b([p({type:String})],h.prototype,"backend",2);b([p({type:Number})],h.prototype,"springBootLiveReloadPort",2);b([p({type:Boolean,attribute:!1})],h.prototype,"expanded",2);b([p({type:Array,attribute:!1})],h.prototype,"messages",2);b([p({type:String,attribute:!1})],h.prototype,"splashMessage",2);b([p({type:Array,attribute:!1})],h.prototype,"notifications",2);b([p({type:String,attribute:!1})],h.prototype,"frontendStatus",2);b([p({type:String,attribute:!1})],h.prototype,"javaStatus",2);b([f()],h.prototype,"tabs",2);b([f()],h.prototype,"activeTab",2);b([f()],h.prototype,"serverInfo",2);b([f()],h.prototype,"features",2);b([f()],h.prototype,"unreadErrors",2);b([Z(".window")],h.prototype,"root",2);b([Z("vaadin-dev-tools-component-picker")],h.prototype,"componentPicker",2);b([f()],h.prototype,"componentPickActive",2);b([f()],h.prototype,"themeEditorState",2);customElements.get("vaadin-dev-tools")===void 0&&customElements.define("vaadin-dev-tools",h);export{y as E,gt as f,ft as i,T as p,U as s,F as t};
