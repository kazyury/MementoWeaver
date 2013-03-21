select * from mw.material;
select * from MW.TAGGED_MATERIAL;
delete from mw.tagged_material;
delete from mw.material;
insert into MW.TAGGED_MATERIAL values ('20130203063324','albums','test','1');
insert into MW.TAGGED_MATERIAL values ('20130203063324','kazunori','sample','1');

select * from MW.PREDEFINED_TAG;
insert into MW.PREDEFINED_TAG values('album','nobugs.nolife.mw.generator.AlbumGenerator');
insert into MW.PREDEFINED_TAG values('winner','nobugs.nolife.mw.generator.WinnerGenerator');
insert into MW.PREDEFINED_TAG values('treasure','nobugs.nolife.mw.generator.TreasureGenerator');
insert into MW.PREDEFINED_TAG values('party','nobugs.nolife.mw.generator.PartyGenerator');
insert into MW.PREDEFINED_TAG values('kazunori','nobugs.nolife.mw.generator.ChronicleGenerator');
insert into MW.PREDEFINED_TAG values('hiroko','nobugs.nolife.mw.generator.ChronicleGenerator');
insert into MW.PREDEFINED_TAG values('taito','nobugs.nolife.mw.generator.ChronicleGenerator');
