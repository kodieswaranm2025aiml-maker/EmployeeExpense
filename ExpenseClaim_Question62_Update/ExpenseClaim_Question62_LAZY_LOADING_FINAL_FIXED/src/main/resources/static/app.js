const API='/api';
let employees=[],policies=[],page=0,lastPage=0;
const $=id=>document.getElementById(id);
const money=n=>new Intl.NumberFormat('en-IN',{style:'currency',currency:'INR',maximumFractionDigits:2}).format(Number(n||0));
async function api(url,options={}){const r=await fetch(API+url,{headers:{'Content-Type':'application/json',...(options.headers||{})},...options});let data=null;try{data=await r.json()}catch{}if(!r.ok)throw new Error(data?.message||'Request failed');return data}
function toast(msg,error=false){const t=$('toast');t.textContent=msg;t.className='toast show'+(error?' error':'');clearTimeout(window._toast);window._toast=setTimeout(()=>t.className='toast',3500)}
async function init(){try{[employees,policies]=await Promise.all([api('/employees'),api('/policies')]);fillActors();renderPolicies();addItem();await loadAll();await loadAudit();}catch(e){toast(e.message,true)}}
function fillActors(){const emp=$('employeeId'),actors=$('actorSelect');emp.innerHTML=employees.filter(e=>e.role==='EMPLOYEE').map(e=>`<option value="${e.id}">${e.name} · ${e.employeeCode}</option>`).join('');actors.innerHTML='';updateActors();$('roleSelect').onchange=updateActors}
function updateActors(){const role=$('roleSelect').value;const list=employees.filter(e=>e.role===role);$('actorSelect').innerHTML=list.map(e=>`<option value="${e.id}">${e.name}</option>`).join('');if(role==='EMPLOYEE'&&list.length)$('employeeId').value=list[0].id}
function renderPolicies(){ $('policies').innerHTML=policies.map(p=>`<div class="policy"><span>${p.category}</span><strong>${money(p.maxAmount)}</strong></div>`).join('') }
function policy(category){return policies.find(p=>p.category.toLowerCase()===category.toLowerCase())}
function addItem(data={category:policies[0]?.category||'Travel',description:'',amount:''}){const wrap=document.createElement('div');wrap.className='item';wrap.innerHTML=`<label>Category<select class="cat">${policies.map(p=>`<option ${p.category===data.category?'selected':''}>${p.category}</option>`).join('')}</select><div class="item-policy">Policy limit: <b>${money(policy(data.category)?.maxAmount||0)}</b></div></label><label>Description<input class="desc" placeholder="What was this expense for?" value="${esc(data.description)}"></label><label>Amount<input class="amt" type="number" min="0.01" step="0.01" placeholder="0.00" value="${data.amount}"></label><button type="button" class="remove" title="Remove">×</button><div class="flag"></div>`;wrap.querySelector('.remove').onclick=()=>{wrap.remove();recalc()};wrap.querySelectorAll('select,input').forEach(x=>{x.addEventListener('input',recalc);x.addEventListener('change',recalc);keyupHandler(x)});wrap.querySelector('.cat').addEventListener('change',()=>{const p=policy(wrap.querySelector('.cat').value);wrap.querySelector('.item-policy').innerHTML=`Policy limit: <b>${money(p?.maxAmount||0)}</b>`;recalc()});$('items').appendChild(wrap);recalc()}
function esc(s){return String(s||'').replaceAll('&','&amp;').replaceAll('"','&quot;').replaceAll('<','&lt;').replaceAll('>','&gt;')}
function keyupHandler(x){x.addEventListener('keyup',recalc)}
function numericValue(el){const n=Number.parseFloat(el?.value);return Number.isFinite(n)?n:0}
function collectItems(){return [...document.querySelectorAll('.item')].map(row=>({row,category:row.querySelector('.cat').value,description:row.querySelector('.desc').value.trim(),amount:numericValue(row.querySelector('.amt'))}))}
function recalc(){let total=0,viol=0;for(const x of collectItems()){const p=policy(x.category);const over=p&&x.amount>Number(p.maxAmount);x.row.classList.toggle('over',!!over);x.row.querySelector('.flag').textContent=over?`⚠ Policy violation · limit ${money(p.maxAmount)}`:'';total+=x.amount;viol+=over?1:0}$('claimTotal').textContent=money(total);$('policySummary').textContent=viol?`${viol} item${viol>1?'s':''} exceed policy · manager override required`:(total?'All items within policy limits':'Add an item')}
$('addItem').onclick=()=>addItem();
$('claimForm').onsubmit=async e=>{
  e.preventDefault();
  recalc();
  const items=collectItems();
  const employeeId=numericValue($('employeeId'));
  let title=$('claimTitle').value.trim();
  if(!employeeId) return toast('Please select an employee.',true);
  if(!title) { title='Expense Reimbursement Claim'; $('claimTitle').value=title; }
  if(!items.length) return toast('Add at least one expense item.',true);
  const bad=items.find(x=>!x.description||x.amount<=0);
  if(bad) return toast('Complete every expense item with description and amount greater than 0.',true);
  try{
    const c=await api('/claims',{method:'POST',body:JSON.stringify({employeeId,title,description:$('claimDescription').value.trim(),items:items.map(x=>({category:x.category,description:x.description,amount:x.amount}))})});
    toast(`Claim ${c.claimNumber} submitted successfully.`);
    $('claimForm').reset();$('items').innerHTML='';addItem();await loadAll();await loadAudit();
  }catch(e){toast(e.message,true)}
};
function badge(status){const cls={PENDING_MANAGER:'b-pending',APPROVED:'b-approved',REJECTED:'b-rejected',PAID:'b-paid'}[status]||'';return `<span class="badge ${cls}">${status.replace('_',' ')}</span>`}
async function loadAll(){try{const status=$('statusFilter').value;const [sortBy,direction]=$('sortFilter').value.split(',');const q=`/claims?page=${page}&size=10${status?'&status='+status:''}&sortBy=${sortBy}&direction=${direction}`;const [all,pending,approved,dash]=await Promise.all([api(q),api('/claims/stage/PENDING_MANAGER?page=0&size=20'),api('/claims/stage/APPROVED?page=0&size=20'),api('/claims/dashboard')]);lastPage=all.totalPages-1;$('pageInfo').textContent=`Page ${all.number+1} of ${Math.max(all.totalPages,1)}`;$('prevPage').disabled=all.first;$('nextPage').disabled=all.last;$('heroCount').textContent=dash.pending;$('sPending').textContent=dash.pending;$('sApproved').textContent=dash.approved;$('sPaid').textContent=dash.paid;$('sViolations').textContent=dash.violations;$('historyBody').innerHTML=all.content.length?all.content.map(c=>`<tr><td><b>${c.claimNumber}</b></td><td>${c.employeeName}</td><td>${esc(c.title)}</td><td class="money">${money(c.totalAmount)}</td><td>${badge(c.status)}</td><td>${c.policyViolation?'<span class="badge b-flag">FLAGGED</span>':'<span class="badge b-approved">PASS</span>'}</td></tr>`).join(''):`<tr><td colspan="6">No claims found.</td></tr>`;$('pendingBody').innerHTML=pending.content.length?pending.content.map(c=>`<tr><td><b>${c.claimNumber}</b><br><small>${esc(c.title)}</small></td><td>${c.employeeName}</td><td class="money">${money(c.totalAmount)}</td><td>${c.policyViolation?'<span class="badge b-flag">OVERRIDE</span>':'<span class="badge b-approved">PASS</span>'}</td><td><div class="row-actions"><button type="button" class="primary" data-review-id="${c.id}" data-violation="${c.policyViolation}">Review</button></div></td></tr>`).join(''):`<tr><td colspan="5">No pending claims.</td></tr>`;$('approvedBody').innerHTML=approved.content.length?approved.content.map(c=>`<tr><td><b>${c.claimNumber}</b></td><td>${c.employeeName}</td><td class="money">${money(c.totalAmount)}</td><td>${badge(c.status)}</td><td><button type="button" class="primary" data-pay-id="${c.id}" data-number="${esc(c.claimNumber)}" data-total="${c.totalAmount}">Pay</button></td></tr>`).join(''):`<tr><td colspan="5">No approved claims waiting for finance.</td></tr>`}catch(e){toast(e.message,true)}}
async function loadAudit(){try{const data=await api('/audit?page=0&size=8');$('auditList').innerHTML=data.content.length?data.content.map(a=>`<div class="audit"><div><strong>${a.action}</strong><p>${esc(a.details||'')} · ${esc(a.actor)}</p></div><time>${new Date(a.createdAt).toLocaleString()}</time></div>`).join(''):`<div class="audit"><div><strong>No activity yet</strong><p>Submit a claim to create the first audit event.</p></div></div>`}catch(e){}}
async function openDecision(id,mode,violation){
  const managers=employees.filter(e=>e.role==='MANAGER');
  if(!managers.length)return toast('No manager user is available.',true);
  let current=null;
  try{current=await api(`/claims/${id}`)}catch(e){return toast(e.message,true)}
  const status=current.status;
  const isPending=status==='PENDING_MANAGER';
  const isApproved=status==='APPROVED';
  $('roleSelect').value='MANAGER';updateActors();$('modal').classList.remove('hidden');
  if(!isPending){
    $('modalContent').innerHTML=`<h3>Claim status</h3><p><b>${esc(current.claimNumber)}</b> is currently <b>${esc(status.replaceAll('_',' '))}</b>. This claim is not waiting for a manager decision.</p><div class="actions"><button type="button" class="primary" onclick="closeModal()">Close</button></div>`;
    return;
  }
  $('modalContent').innerHTML=`<h3>Manager decision</h3><p>${violation?'This claim contains an over-limit item. Approval requires an explicit manager override.':'Review the claim and record your remarks.'}</p><label>Manager<select id="decisionManager">${managers.map(m=>`<option value="${m.id}">${esc(m.name)} · ${esc(m.employeeCode)}</option>`).join('')}</select></label><label>Remarks<textarea id="decisionRemarks" rows="3" placeholder="Explain your decision...">Verified</textarea></label>${violation?`<label>Manager override<select id="override"><option value="false">No — do not override</option><option value="true">Yes — override policy violation</option></select></label>`:`<input type="hidden" id="override" value="false">`}<div class="actions"><button type="button" class="ghost" onclick="closeModal()">Cancel</button><button type="button" class="ghost" onclick="submitDecision(${id},'reject')">Reject</button><button type="button" class="primary" onclick="submitDecision(${id},'approve')">Approve</button></div>`;
}
async function submitDecision(id,mode){
  const managerEl=$('decisionManager');const managerId=Number(managerEl?managerEl.value:$('actorSelect').value);
  if(!managerId)return toast('Select a manager first.',true);
  const remarks=($('decisionRemarks')?.value||'').trim();const override=($('override')?.value||'false')==='true';
  if(!remarks)return toast('Manager remarks are required.',true);
  try{
    await api(`/claims/${id}/${mode}`,{method:'POST',body:JSON.stringify({managerId,overridePolicy:override,remarks})});
    closeModal();toast(`Claim ${mode==='approve'?'approved':'rejected'} successfully.`);
    await loadAll();await loadAudit();
  }catch(e){toast(e.message,true)}
}
document.addEventListener('click',e=>{
  const btn=e.target.closest('#pendingBody button[data-review-id]');
  if(btn){e.preventDefault();e.stopPropagation();openDecision(Number(btn.dataset.reviewId),'approve',btn.dataset.violation==='true');}
  const pay=e.target.closest('#approvedBody button[data-pay-id]');
  if(pay){e.preventDefault();e.stopPropagation();openPayment(Number(pay.dataset.payId),pay.dataset.number,Number(pay.dataset.total));}
});
function openPayment(id,number,total){$('modal').classList.remove('hidden');$('modalContent').innerHTML=`<h3>Finance payment</h3><p><b>${number}</b> · ${money(total)}. Finance can pay only an APPROVED claim.</p><label>Payment reference<input id="paymentRef" placeholder="PAY-2026-001"></label><div class="actions"><button class="ghost" onclick="closeModal()">Cancel</button><button class="primary" onclick="submitPayment(${id})">Mark as paid</button></div>`}
async function submitPayment(id){const financeId=Number($('actorSelect').value);const ref=$('paymentRef').value.trim();if(!financeId)return toast('Switch to Finance role first.',true);if(!ref)return toast('Payment reference is required.',true);try{await api(`/claims/${id}/pay`,{method:'PUT',body:JSON.stringify({financeId,paymentReference:ref})});closeModal();toast('Payment recorded. Claim is now PAID.');await loadAll();await loadAudit()}catch(e){toast(e.message,true)}}
function closeModal(){$('modal').classList.add('hidden')}
function fillDemo(violation){$('roleSelect').value='EMPLOYEE';updateActors();$('employeeId').value=employees.find(e=>e.role==='EMPLOYEE')?.id||'';$('claimTitle').value=violation?'Emergency travel claim':'Client visit expenses';$('claimDescription').value=violation?'Travel above standard policy limit for an urgent client visit.':'Travel and food expenses for a client visit.';$('items').innerHTML='';addItem({category:'Travel',description:violation?'Urgent client travel':'Train/bus travel',amount:violation?7000:3500});addItem({category:'Food',description:'Client lunch',amount:1200});window.scrollTo({top:document.querySelector('.employee-panel').offsetTop-20,behavior:'smooth'});recalc()}
$('prevPage').onclick=()=>{if(page>0){page--;loadAll()}};$('nextPage').onclick=()=>{if(page<lastPage){page++;loadAll()}};$('statusFilter').onchange=()=>{page=0;loadAll()};$('sortFilter').onchange=()=>{page=0;loadAll()};
init();
