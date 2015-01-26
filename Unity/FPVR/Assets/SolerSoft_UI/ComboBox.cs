using SolerSoft.UI;
using System.Collections;
using UnityEngine;
using UnityEngine.UI;

public class ComboBox : Selector
{
    public Text selection;
    public GameObject listBox;

    // Use this for initialization
    private void Start()
    {
        base.Initialize();
        this.selection = (Text)this.transform.FindChild("SelectionButton").transform.FindChild("Text").GetComponent<Text>();
        var sc = this.transform.FindChild("ScrollContainer").gameObject;
        this.listBox.SetActive(false);
		this.gameObject.SetActive(false);
		this.gameObject.SetActive(true);
    }

    //// Update is called once per frame
    //private void Update()
    //{
    //}

    public void ShowListBox()
    {
        this.listBox.SetActive(true);
    }

    public void SelectItem(Text value)
    {
        this.selection.text = value.text;
        this.listBox.SetActive(false);
    }
}