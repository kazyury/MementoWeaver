select * from MW.MATERIAL;
select * from MW.TAGGED_MATERIAL;
select * from MW.MEMENTO;
select * from MW.MEMENTO_CONTENTS;

delete from MW.MEMENTO_CONTENTS;
delete from MW.TAGGED_MATERIAL;
delete from MW.MEMENTO;
delete from MW.MATERIAL;
delete from MW.SCANNED_RESULTS

select * from MW.PREDEFINED_TAG;


delete from mw.memento_contents where material_id='20130203063324' and tag='album';
delete from mw.tagged_material where material_id='20130203063324' and tag='album';

insert into MW.MEMENTO_CONTENTS values('20130103085623','album','a_201301');
insert into MW.MEMENTO_CONTENTS values('20130203063324','hiroko','hiroko');
insert into MW.MEMENTO_CONTENTS values('20130203063324','party','p_2013');  
insert into MW.MEMENTO_CONTENTS values('20130203063416','treasure','t_age10'); 
insert into MW.MEMENTO_CONTENTS values('20130203063416','winner','w_201302');


