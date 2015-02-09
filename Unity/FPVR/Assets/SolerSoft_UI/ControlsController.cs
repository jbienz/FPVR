using SolerSoft.UI;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using UnityEngine;

namespace Assets.SolerSoft_UI
{
    public class ControlsController : MonoBehaviour
    {
        public ComboBox comboBox;
        private ObservableCollection<string> items;

        private void Start()
        {
            items = new ObservableCollection<string>();
            comboBox.ItemsSource = items;
        }

        public void AddOne()
        {
            items.Add(DateTime.Now.Ticks.ToString());
        }
    }
}
