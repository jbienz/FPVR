using System.Collections;
using UnityEngine;
using UnityEngine.UI;

public class ListBox : MonoBehaviour
{
    public Text selection;
    public GameObject listBox;

    // Use this for initialization
    private void Start()
    {
        this.selection = (Text)this.transform.FindChild("SelectionButton").transform.FindChild("Text").GetComponent<Text>();
        this.listBox = this.transform.FindChild("List-Items").gameObject;
        this.listBox.SetActive(false);
    }

    // Update is called once per frame
    private void Update()
    {
    }

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