// 根据name和value反选对应复选框（单个值）
function oppCheckedByValue(name, value)
{
	var chks = document.getElementsByName(name);
	if (chks)
	{
		var length = chks.length;
		for (var i = 0; i < length; i++)
		{
			if (chks[i].type == "checkbox" && chks[i].value == value)
			{
				chks[i].checked = !chks[i].checked;
				break;
			}
		}
	}
}

// 根据name和value反选对应复选框（多个值）
function oppCheckedByValues(name, values)
{
	var chks = document.getElementsByName(name);
	if (chks && values && values.length)
	{
		var length = chks.length;
		for (var i = 0; i < length; i++)
		{
			if (chks[i].type == "checkbox")
			{
				for (var j = 0; j < values.length; j++)
				{
					if (chks[i].value == values[j])
					{
						chks[i].checked = !chks[i].checked;
						break;
					}
				}
			}
		}
	}
}