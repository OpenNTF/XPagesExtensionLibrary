var isIframe = false;
var isAndroid = false;


function isMobile()
{
	var uAgent = context.getUserAgent().getUserAgent();

	if (((uAgent.match("iPhone") !== null || param.platform=="iphone") || (uAgent.match("Android") !== null || param.platform=="android") || uAgent.match("iPad") !== null) || isIframe == true)
	{
		//print("iframe");
		return true;
	}	
	else
	{
		//print("Not iframe");
		return false;
	}
}

function isAndroidCheck()
{
	var uAgent = context.getUserAgent().getUserAgent();

	if ((uAgent.match("Android") !== null || param.platform=="android") || isAndroid == true)
	{
		return true;
	}	
	else
	{
		return false;
	}
}

function isIpad()
{
	var uAgent = context.getUserAgent().getUserAgent();

	if (uAgent.match("iPad") !== null)
	{
		return true;
	}	
	else
	{
		return false;
	}
}

function isIphone()
{
	var uAgent = context.getUserAgent().getUserAgent();

	if (uAgent.match("iPhone") !== null || param.platform=="iphone")
	{
		return true;
	}	
	else
	{
		return false;
	}
}

function setIframe(value)
{
	isIframe = value;	
}

function setAndroid(value)
{
	isAndroid = value;	
}