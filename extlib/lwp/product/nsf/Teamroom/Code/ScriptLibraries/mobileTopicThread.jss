var list;
var temp = "";
var position = "";

function topicThreadInit()
{
	list = new java.util.HashMap();
}

function setPosition(pos, value)
{
	temp = "" + pos;
	position = temp.substr(temp.indexOf('.')+1, temp.length-1);
	list[position] = value;
}

function getPosition(pos)
{
	temp = "" + pos;
	position = temp.substr(temp.indexOf('.')+1, temp.length-1);
	return list[position];	
}