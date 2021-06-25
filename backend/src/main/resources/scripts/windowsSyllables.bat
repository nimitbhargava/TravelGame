@ECHO OFF
echo %1
call activate py37
python "C:\\Users\\Martin Karjus\\IdeaProjects\\iapb\\backend\\src\\main\\resources\\scripts\\syllables.py" "%1"
call conda deactivate
ECHO bat end
