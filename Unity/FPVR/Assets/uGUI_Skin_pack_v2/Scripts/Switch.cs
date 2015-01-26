using System.Collections;
using UnityEngine;
using UnityEngine.UI;

public class Switch : MonoBehaviour
{
    public GameObject txt_ON;
    public GameObject txt_OFF;

    // Use this for initialization
    private void Start()
    {
        this.txt_ON = (GameObject)this.transform.FindChild("Text-ON").gameObject;
        this.txt_OFF = (GameObject)this.transform.FindChild("Text-OFF").gameObject;
    }

    // Update is called once per frame
    public void OnChangeValue(Scrollbar value)
    {
        if (value.value == 0)
        {
            this.txt_ON.SetActive(false);
            this.txt_OFF.SetActive(true);
        }
        if (value.value == 1)
        {
            this.txt_ON.SetActive(true);
            this.txt_OFF.SetActive(false);
        }
    }
}