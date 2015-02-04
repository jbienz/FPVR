using SolerSoft.UI;
using System.Collections;
using UnityEngine;
using UnityEngine.UI;

public class ComboBox : Selector
{
    public Text selectionText;
    public GameObject itemsContainer;

    // Use this for initialization
    private void Start()
    {
        base.Initialize();
        if (selectionText == null)
        {
            selectionText = (Text)transform.Find("SelectionButton").transform.Find("Text").GetComponent<Text>();
        }
        if (itemsContainer == null)
        {
            itemsContainer = transform.Find("ItemsContainer").gameObject;
        }
        this.itemsContainer.SetActive(false);
		this.gameObject.SetActive(false);
		this.gameObject.SetActive(true);
    }

    //// Update is called once per frame
    //private void Update()
    //{
    //}

    public void ShowListBox()
    {
        this.itemsContainer.SetActive(true);
    }

    public void SelectItem(Text value)
    {
        this.selectionText.text = value.text;
        this.itemsContainer.SetActive(false);
    }
}